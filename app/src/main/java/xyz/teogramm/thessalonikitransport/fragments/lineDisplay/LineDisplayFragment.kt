package xyz.teogramm.thessalonikitransport.fragments.lineDisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransport.databinding.FragmentLineDisplayBinding
import xyz.teogramm.thessalonikitransport.viewModels.LinesRoutesViewModel

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
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                model.linesWithRoutes.collect{linesWithRoutes->
                    recyclerView.adapter = LineRecyclerViewAdapter(linesWithRoutes)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}