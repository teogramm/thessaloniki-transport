package xyz.teogramm.thessalonikitransport.fragments.routeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransport.databinding.FragmentRouteDetailsBinding
import xyz.teogramm.thessalonikitransport.viewModels.RouteViewModel

class RouteDetailsFragment: Fragment() {

    private val routeViewModel: RouteViewModel by activityViewModels()

    private var _binding: FragmentRouteDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    routeViewModel.selectedLine.collectLatest { line ->
                        binding.lineNumber.text = line.number
                    }
                }
                launch {
                    routeViewModel.selectedRoute.collectLatest { route ->
                        binding.routeName.text = route.nameEL
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pager.adapter = RouteViewPagerAdapter(this)
        // TabLayout tab text
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = RouteViewPagerAdapter.getTabText(requireContext(), position)
        }.attach()
        // Enable swiping only on stops tab (position 0)
        binding.pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Disable viewpager swiping for map and schedule widget
                binding.pager.isUserInputEnabled = position == 0
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}