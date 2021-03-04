package xyz.teogramm.thessalonikitransit.fragments.routeDetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RouteViewPagerAdapter(f: Fragment): FragmentStateAdapter(f) {
    override fun getItemCount(): Int {
        return 1
    }

    override fun createFragment(position: Int): Fragment {
        return StopListFragment()
    }
}