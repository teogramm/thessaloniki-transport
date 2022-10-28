package xyz.teogramm.thessalonikitransit.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewAlertsStopBinding
import xyz.teogramm.thessalonikitransit.fragments.stopDetails.ArrivalTimesRecyclerViewAdapter

/**
 * RecyclerView for displaying stops along with their enabled alerts.
 * @param onEditButtonPressed Function that is executed each time the edit button is pressed for a stop. Takes
 *        the stop as a parameter.
 */
class AlertRecyclerViewAdapter(private val stopsWithAlerts: List<StopAlerts>,
                               private val onEditButtonPressed: (Stop) -> Unit) :
    RecyclerView.Adapter<AlertRecyclerViewAdapter.StopAlertsRecyclerViewHolder>() {

    class StopAlertsRecyclerViewHolder(private val binding: RecyclerviewAlertsStopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val editButton = binding.editStopAlertsButton
        private val stopNameText = binding.alertStopName
        private val lineRecyclerView = binding.alertLineRecyclerView

        fun bind(stopAlerts: StopAlerts){
            stopNameText.text = stopAlerts.stop.nameEL
            lineRecyclerView.adapter = ArrivalTimesRecyclerViewAdapter(stopAlerts.lines)
        }

        /**
         * Set the function to be called when the edit button is pressed for a stop.
         */
        fun setOnEditButtonPressedListener(action: (View) -> Unit){
            editButton.setOnClickListener(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopAlertsRecyclerViewHolder {
        val binding =
            RecyclerviewAlertsStopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StopAlertsRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return stopsWithAlerts.size
    }

    override fun onBindViewHolder(holder: StopAlertsRecyclerViewHolder, position: Int) {
        // Make the edit button call the onEditButtonPressed function with the corresponding stop as an argument.
        holder.setOnEditButtonPressedListener {
            onEditButtonPressed(stopsWithAlerts[position].stop)
        }
        holder.bind(stopsWithAlerts[position])
    }
}