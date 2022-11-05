package xyz.teogramm.thessalonikitransport.fragments.stopDetails

import androidx.recyclerview.widget.DiffUtil
import xyz.teogramm.thessalonikitransport.database.transit.entities.Line
import xyz.teogramm.thessalonikitransport.database.transit.entities.Route

data class RouteWithLineAndArrivalTime(
    val route: Route, val line: Line, val arrivalTimes: List<Int>?
)

class RouteWithLineAndArrivalTimeDiffCallback(
    private val oldRoutes: List<RouteWithLineAndArrivalTime>, private val newRoutes: List<RouteWithLineAndArrivalTime>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldRoutes.size
    }

    override fun getNewListSize(): Int {
        return newRoutes.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldRoutes[oldItemPosition]
        val new = newRoutes[newItemPosition]
        return old.line.lineId == new.line.lineId && old.route.routeId == new.route.routeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldRoutes[oldItemPosition]
        val new = newRoutes[newItemPosition]
        return old.arrivalTimes == new.arrivalTimes
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val old = oldRoutes[oldItemPosition]
        val new = newRoutes[newItemPosition]
        return if(old.arrivalTimes != new.arrivalTimes) true else null
    }

}