package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.teogramm.thessalonikitransit.databinding.FragmentStopDetailsBinding
import xyz.teogramm.thessalonikitransit.viewModels.StopViewModel

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
        stopViewModel.getStop().observe(viewLifecycleOwner,{ stop ->
            binding.stopName.text = stop.nameEL
        })
        stopViewModel.getStopLinesWithArrivalTimes().observe(viewLifecycleOwner,{ linesWithArrivalTimes ->
            arrivalTimesRecyclerView.adapter = ArrivalTimesRecyclerViewAdapter(linesWithArrivalTimes)
        })
        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}