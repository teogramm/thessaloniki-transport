package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewStopLineArrivalBinding


class ArrivalTimesRecyclerViewAdapter(routes: List<RouteWithLineAndArrivalTime>):
        RecyclerView.Adapter<ArrivalTimesRecyclerViewAdapter.LineArrivalTimesViewHolder>() {

    private val routes = routes.toMutableList()

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
            setTime(route.arrivalTimes)
        }

        fun setTime(times: List<Int>?){
            arrivalTimeTextView.text = times?.min()?.toString() ?: "-"
        }
    }

    fun setItems(newRoutes: List<RouteWithLineAndArrivalTime>){
        val diffCallback = RouteWithLineAndArrivalTimeDiffCallback(this.routes, newRoutes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        routes.clear()
        routes.addAll(newRoutes)

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineArrivalTimesViewHolder {
        val binding = RecyclerviewStopLineArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineArrivalTimesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int) {
        holder.bindView(routes[position])
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }else{
            if(payloads[0] == true){
                holder.setTime(routes[position].arrivalTimes)
            }
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}