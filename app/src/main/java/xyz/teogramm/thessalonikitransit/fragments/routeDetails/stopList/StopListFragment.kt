package xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransit.databinding.FragmentRouteDetailsStopsBinding
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel

class StopListFragment: Fragment() {
    private var _binding: FragmentRouteDetailsStopsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =  FragmentRouteDetailsStopsBinding.inflate(inflater, container, false)

        val routeViewModel: RouteViewModel by activityViewModels()
        val stopsRecyclerView = binding.stopRecyclerView
        stopsRecyclerView.layoutManager = LinearLayoutManager(context)
        // Add lines between items
        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        stopsRecyclerView.addItemDecoration(dividerItemDecoration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                routeViewModel.stops.collectLatest { stops ->
                    stopsRecyclerView.adapter = StopListRecyclerViewAdapter(stops)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}