package xyz.teogramm.thessalonikitransit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.teogramm.thessalonikitransit.databinding.FragmentLineDisplayBinding
import xyz.teogramm.thessalonikitransit.recyclerViews.lines.LineRecyclerViewAdapter
import xyz.teogramm.thessalonikitransit.viewModels.LinesRoutesViewModel

/**
 * Fragment displaying all lines and their routes.
 */
@AndroidEntryPoint
class LineDisplayFragment: Fragment() {

    private var _binding: FragmentLineDisplayBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLineDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerView
        val model: LinesRoutesViewModel by viewModels()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        model.getLinesWithRoutes().observe(viewLifecycleOwner, {
            recyclerView.adapter = LineRecyclerViewAdapter(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}