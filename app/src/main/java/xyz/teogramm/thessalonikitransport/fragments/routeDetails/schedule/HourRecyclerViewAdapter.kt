package xyz.teogramm.thessalonikitransport.fragments.routeDetails.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransport.databinding.RecyclerviewScheduleTimeBinding
import java.time.LocalTime

/**
 * Adapter that shows different departure times for an hour of day. For example for hour 09 times 09:10, 09:15 ....
 */
class HourRecyclerViewAdapter(private val times: List<LocalTime> ):
    RecyclerView.Adapter<HourRecyclerViewAdapter.HourViewHolder>() {

    class HourViewHolder(binding: RecyclerviewScheduleTimeBinding): RecyclerView.ViewHolder(binding.root) {
        private val timeTextView = binding.departureTime

        fun bindView(time: LocalTime) {
            timeTextView.text = time.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding = RecyclerviewScheduleTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bindView(times[position])
    }

    override fun getItemCount(): Int {
        return times.size
    }
}