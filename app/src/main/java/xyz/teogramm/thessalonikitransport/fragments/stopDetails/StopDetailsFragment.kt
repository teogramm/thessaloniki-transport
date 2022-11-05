package xyz.teogramm.thessalonikitransport.fragments.stopDetails

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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransport.databinding.FragmentStopDetailsBinding
import xyz.teogramm.thessalonikitransport.fragments.alerts.CreateAlertDialog
import xyz.teogramm.thessalonikitransport.viewModels.StopViewModel


/**
 * Fragment that shows details about a stop.
 * Currently, this includes:
 *  * Routes passing through the stop with the names and numbers of the lines they belong to.
 *  * Bus arrival times, if there is network connectivity.
 *  * Stop location on map.
 *  * Direction/last stop for each route, so multiple routes of the same line can be disambiguated.
 */
class StopDetailsFragment: Fragment() {
    private val stopViewModel: StopViewModel by activityViewModels()

    private var _binding: FragmentStopDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStopDetailsBinding.inflate(inflater, container, false)

        val arrivalTimesRecyclerView = binding.timeRecyclerView
        arrivalTimesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        arrivalTimesRecyclerView.adapter = ArrivalTimesRecyclerViewAdapter(emptyList())
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    stopViewModel.stop.collectLatest { stop ->
                        binding.stopName.text = stop.nameEL
                    }
                }
                launch {
                    stopViewModel.routesWithLineAndArrivalTimes.collectLatest{ routes ->
                        // https://stackoverflow.com/questions/43458146/diffutil-in-recycleview-making-it-autoscroll-if-a-new-item-is-added
                        val recyclerViewState = (arrivalTimesRecyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState()
                        (arrivalTimesRecyclerView.adapter as ArrivalTimesRecyclerViewAdapter).setItems(routes)
                        (arrivalTimesRecyclerView.layoutManager as LinearLayoutManager).onRestoreInstanceState(recyclerViewState)
                    }
                }
            }
        }

        binding.addNotificationButton.setOnClickListener{
            CreateAlertDialog().show(childFragmentManager, null)
        }

        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}