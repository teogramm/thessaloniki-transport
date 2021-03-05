package xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        // Hide stops recycler view and only show it once data is ready
        stopsRecyclerView.visibility = View.INVISIBLE
        stopsRecyclerView.layoutManager = LinearLayoutManager(context)
        // Add lines between items
        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        stopsRecyclerView.addItemDecoration(dividerItemDecoration)

        routeViewModel.stops.observe(viewLifecycleOwner, {
            stopsRecyclerView.adapter = StopListRecyclerViewAdapter(it)
            stopsRecyclerView.visibility = View.VISIBLE
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}