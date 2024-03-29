package xyz.teogramm.thessalonikitransport.fragments.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransport.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransport.databinding.FragmentHomeBinding
import xyz.teogramm.thessalonikitransport.fragments.alerts.CreateAlertDialog
import xyz.teogramm.thessalonikitransport.service.AlertService
import xyz.teogramm.thessalonikitransport.service.AlertServiceActions
import xyz.teogramm.thessalonikitransport.viewModels.HomeViewModel
import xyz.teogramm.thessalonikitransport.viewModels.StopViewModel

/**
 * Home page fragment that displays widgets
 */
class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Implementation of the [AlertActions] interface, which sends the actions performed to the AlertsService
     */
    private val alertActions = object: AlertActions{
        override fun onStopEditButtonPressed(stop: Stop) {
            // When the edit button is pressed set the stop in the viewModel and show the edit dialog.
            stopViewModel.setStop(stop)
            CreateAlertDialog().show(childFragmentManager, null)
        }

        override fun onStopAlertsEnabled(stopAlerts: StopAlerts) {
            // Tell the service to start monitoring this stop
            Intent(requireContext(),AlertService::class.java).apply {
                action = AlertServiceActions.ADD_ALERT.toString()
                putExtra(AlertService.STOPID_EXTRA_NAME, stopAlerts.stop.stopId)
            }.also { intent ->
                requireContext().startForegroundService(intent)
            }
        }

        override fun onStopAlertsDisabled(stopAlerts: StopAlerts) {
            val intent = Intent(requireContext(), AlertService::class.java)
            intent.action = AlertServiceActions.REMOVE_ALERT.toString()
            intent.putExtra(AlertService.STOPID_EXTRA_NAME, stopAlerts.stop.stopId)
            requireContext().startService(intent)
        }

    }

    private val alertRecyclerViewAdapter = AlertRecyclerViewAdapter(emptyList(), alertActions)

    private var alertServiceConnected = false
    private lateinit var alertService: AlertService
    private val alertServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AlertService.AlertServiceBinder
            alertService = binder.getService()
            alertService.setListener(object: AlertService.AlertServiceListener{
                override fun onServiceStopped() {
                    Log.d("HomeFragment","Emptied Enabled Stops")
                    alertRecyclerViewAdapter.setData(newEnabledStops = emptySet())
                }
            })
            alertServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            alertServiceConnected = false
        }

    }

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val stopViewModel by activityViewModels<StopViewModel>()

    override fun onStart() {
        // Bind to AlertsService
        Intent(requireContext(), AlertService::class.java).also{intent ->
            requireContext().bindService(intent, alertServiceConnection, Context.BIND_AUTO_CREATE)
        }
        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.widgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.widgetRecyclerView.adapter = alertRecyclerViewAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.allAlerts.collectLatest {uiState ->
                    // Get the enabled alerts from the service
                    val enabledAlerts = if(alertServiceConnected){
                        alertService.getActiveAlertStopIds().toMutableSet()
                    }else{
                        mutableSetOf()
                    }
                    if(alertServiceConnected){
                        Log.d("HomeFragment", "Alert service connected")
                    }else{
                        Log.d("HomeFragment", "Alert Service disconnected")
                    }
                    alertRecyclerViewAdapter.setData(newStopsWithAlerts = uiState.alerts,
                        newEnabledStops = enabledAlerts)
                }
            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        requireContext().unbindService(alertServiceConnection)
        alertServiceConnected = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}