package xyz.teogramm.thessalonikitransit.fragments.routeDetails.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewScheduleHourBinding
import java.time.LocalTime

/**
 * Adapter that shows different departure times for an hour of day. For example for hour 09 times 09:10, 09:15 ....
 */
class HourRecyclerViewAdapter(groupedTimes: Map<Int,List<LocalTime>> ):
    RecyclerView.Adapter<HourRecyclerViewAdapter.HourViewHolder>() {

    // Convert the grouped times into an array of key-value pairs
    private val times = groupedTimes.map { Pair(it.key,it.value) }.toTypedArray()

    class HourViewHolder(binding: RecyclerviewScheduleHourBinding): RecyclerView.ViewHolder(binding.root) {
        private val timesRecyclerView = binding.departureTimesRecyclerView
        private val hourTextView = binding.hourNumber

        fun bindView(hour: Int, times: List<LocalTime>) {
            hourTextView.text = hour.toString()
            timesRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL,false)
            timesRecyclerView.adapter = TimeRecyclerViewAdapter(times)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding = RecyclerviewScheduleHourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val timesPair = times[position]
        holder.bindView(timesPair.first, timesPair.second)
    }

    override fun getItemCount(): Int {
        return times.size
    }
}