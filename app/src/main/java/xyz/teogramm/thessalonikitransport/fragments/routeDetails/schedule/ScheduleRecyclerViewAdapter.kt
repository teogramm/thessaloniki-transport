package xyz.teogramm.thessalonikitransport.fragments.routeDetails.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransport.databinding.RecyclerviewScheduleCardBinding
import xyz.teogramm.thessalonikitransport.viewModels.ScheduleWithGroupedTimes

/**
 * Adapter that displays the different schedules for a route.
 */
class ScheduleRecyclerViewAdapter(private val lineSchedules: List<ScheduleWithGroupedTimes>):
    RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(binding: RecyclerviewScheduleCardBinding): RecyclerView.ViewHolder(binding.root){
        private val calendarNameTextView = binding.calendarName
        private val hourRecyclerView = binding.hourRecyclerView

        fun bindView(schedule: ScheduleWithGroupedTimes) {
            calendarNameTextView.text = schedule.calendar.nameEL
            hourRecyclerView.layoutManager = GridLayoutManager(itemView.context,6)
            hourRecyclerView.adapter = HourRecyclerViewAdapter(schedule.times)
        }
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bindView(lineSchedules[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = RecyclerviewScheduleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lineSchedules.size
    }
}