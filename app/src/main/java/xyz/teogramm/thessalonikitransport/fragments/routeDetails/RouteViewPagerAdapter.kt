package xyz.teogramm.thessalonikitransport.fragments.routeDetails

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.teogramm.thessalonikitransport.R
import xyz.teogramm.thessalonikitransport.fragments.routeDetails.schedule.ScheduleFragment
import xyz.teogramm.thessalonikitransport.fragments.routeDetails.stopList.StopListFragment
import xyz.teogramm.thessalonikitransport.fragments.routeDetails.stopMap.StopMapFragment

class RouteViewPagerAdapter(f: Fragment): FragmentStateAdapter(f) {

    companion object {
        /**
         * Returns the text shown for tab at the given position
         */
        fun getTabText(context: Context, position: Int): String {
            return when (position){
                0 -> context.getString(R.string.stops_tab)
                1 -> context.getString(R.string.map_tab)
                2 -> context.getString(R.string.schedule_tab)
                else -> "error"
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> StopListFragment()
            1 -> StopMapFragment()
            else -> ScheduleFragment()
        }
    }
}