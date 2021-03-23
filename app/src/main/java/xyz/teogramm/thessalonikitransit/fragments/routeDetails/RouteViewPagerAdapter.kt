package xyz.teogramm.thessalonikitransit.fragments.routeDetails

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.fragments.routeDetails.schedule.ScheduleFragment
import xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopList.StopListFragment

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
            1 -> Fragment()
            else -> ScheduleFragment()
        }
    }
}