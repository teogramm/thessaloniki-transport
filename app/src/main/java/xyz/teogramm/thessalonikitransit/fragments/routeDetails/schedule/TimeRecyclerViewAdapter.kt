package xyz.teogramm.thessalonikitransit.fragments.routeDetails.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewScheduleTimeBinding
import java.time.LocalTime

/**
 * RecyclerView adapter that shows a list of [LocalTime]s
 */
class TimeRecyclerViewAdapter(private val times: List<LocalTime>):
    RecyclerView.Adapter<TimeRecyclerViewAdapter.TimeViewHolder>() {

    class TimeViewHolder(binding: RecyclerviewScheduleTimeBinding): RecyclerView.ViewHolder(binding.root) {
        private val timeTextView = binding.departureTime

        fun bindView(time: LocalTime) {
            timeTextView.text = time.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = RecyclerviewScheduleTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bindView(times[position])
    }

    override fun getItemCount(): Int {
        return times.size
    }
}