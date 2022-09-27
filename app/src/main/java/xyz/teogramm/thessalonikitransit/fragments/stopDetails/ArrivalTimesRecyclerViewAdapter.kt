package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewStopLineArrivalBinding

class ArrivalTimesRecyclerViewAdapter(private var routes: List<RouteWithLineAndArrivalTime>):
        RecyclerView.Adapter<ArrivalTimesRecyclerViewAdapter.LineArrivalTimesViewHolder>() {


    class LineArrivalTimesViewHolder(binding: RecyclerviewStopLineArrivalBinding): RecyclerView.ViewHolder(binding.root) {
        private val lineNumberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val arrivalTimeTextView = binding.arrivalTime
        private val routeTextView = binding.routeName

        fun bindView(route: RouteWithLineAndArrivalTime) {
            lineNumberTextView.text = route.line.number
            lineNameTextView.text = route.line.nameEL
            routeTextView.text = route.route.nameEL
            // TODO: Show all arrival times
            arrivalTimeTextView.text = route.arrivalTimes?.min()?.toString() ?: "-"
        }
    }

    fun setItems(newRoutes: List<RouteWithLineAndArrivalTime>){
        routes = newRoutes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineArrivalTimesViewHolder {
        val binding = RecyclerviewStopLineArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineArrivalTimesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int) {
        holder.bindView(routes[position])
    }

    override fun onViewRecycled(holder: LineArrivalTimesViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}