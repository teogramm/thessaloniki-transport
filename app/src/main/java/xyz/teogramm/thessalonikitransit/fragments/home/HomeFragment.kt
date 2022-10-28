package xyz.teogramm.thessalonikitransit.fragments.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
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
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.databinding.FragmentHomeBinding
import xyz.teogramm.thessalonikitransit.fragments.alerts.CreateAlertDialog
import xyz.teogramm.thessalonikitransit.viewModels.HomeViewModel
import xyz.teogramm.thessalonikitransit.viewModels.StopViewModel

/**
 * Home page fragment that displays widgets
 */
class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val stopViewModel by activityViewModels<StopViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // When the edit button is pressed set the stop in the viewModel and show the edit dialog.
        val onEditButtonPressed = fun(s: Stop){
            stopViewModel.setStop(s)
            CreateAlertDialog().show(childFragmentManager, null)
        }

        binding.widgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.allAlerts.collectLatest {uiState ->
                    binding.widgetRecyclerView.adapter = AlertRecyclerViewAdapter(uiState.alerts, onEditButtonPressed)
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}