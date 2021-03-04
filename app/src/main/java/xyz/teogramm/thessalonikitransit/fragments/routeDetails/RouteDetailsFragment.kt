package xyz.teogramm.thessalonikitransit.fragments.routeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.databinding.FragmentOnboardingBinding
import xyz.teogramm.thessalonikitransit.databinding.FragmentRouteDetailsBinding
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel

class RouteDetailsFragment: Fragment() {

    private val routeViewModel: RouteViewModel by activityViewModels()

    private var _binding: FragmentRouteDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRouteDetailsBinding.inflate(inflater, container, false)
        binding.pager.adapter = RouteViewPagerAdapter(this)
        // TabLayout tab text
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = getTabText(position)
        }.attach()

        binding.lineNumber.text = routeViewModel.getSelectedLineNumber()
        binding.routeName.text = routeViewModel.getSelectedRouteName()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Returns the text shown for tab at the given position
     */
    private fun getTabText(position: Int): String {
        return when (position){
            1 -> getString(R.string.stops_tab)
            2 -> getString(R.string.map_tab)
            3 -> getString(R.string.schedule_tab)
            else -> "error"
        }
    }
}