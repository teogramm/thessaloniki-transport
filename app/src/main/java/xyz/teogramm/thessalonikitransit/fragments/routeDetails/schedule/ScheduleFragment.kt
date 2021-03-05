package xyz.teogramm.thessalonikitransit.fragments.routeDetails.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.teogramm.thessalonikitransit.databinding.FragmentRouteDetailsScheduleBinding
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel

/**
 * Fragment for displaying departure information about a route.
 */
class ScheduleFragment: Fragment() {
    private var _binding: FragmentRouteDetailsScheduleBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRouteDetailsScheduleBinding.inflate(inflater, container, false)

        val recyclerView = binding.calendarRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val routeViewModel: RouteViewModel by activityViewModels()
        routeViewModel.schedules.observe(viewLifecycleOwner, {
            recyclerView.adapter = ScheduleRecyclerViewAdapter(it)
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}