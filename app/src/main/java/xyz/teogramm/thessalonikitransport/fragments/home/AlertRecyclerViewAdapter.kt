package xyz.teogramm.thessalonikitransport.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransport.databinding.RecyclerviewAlertsStopBinding
import xyz.teogramm.thessalonikitransport.fragments.stopDetails.ArrivalTimesRecyclerViewAdapter

/**
 * RecyclerView for displaying stops along with their enabled alerts.
 */
class AlertRecyclerViewAdapter(private var stopsWithAlerts: List<StopAlerts>,
                               private val alertActions: AlertActions,
                               private var enabledStops: MutableSet<Int> = mutableSetOf()) :
    RecyclerView.Adapter<AlertRecyclerViewAdapter.StopAlertsRecyclerViewHolder>() {

    class StopAlertsRecyclerViewHolder(private val binding: RecyclerviewAlertsStopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val editButton = binding.editStopAlertsButton
        private val stopNameText = binding.alertStopName
        private val lineRecyclerView = binding.alertLineRecyclerView
        private val alertEnabledSwitch = binding.alertEnableSwitch
        private var onAlertEnable = fun(){}
        private var onAlertDisable = fun(){}

        init{
            alertEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    onAlertEnable()
                }else{
                    onAlertDisable()
                }
            }
        }

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

        fun setOnAlertEnabledAction(action: () -> Unit){
            onAlertEnable = action
        }

        fun setOnAlertDisabledAction(action: () -> Unit){
            onAlertDisable = action
        }

        fun setToggle(enabled: Boolean){
            alertEnabledSwitch.isChecked = enabled
        }

        fun resetCallbacks(){
            onAlertEnable = fun(){}
            onAlertDisable = fun(){}
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
        val currentStopWithAlerts = stopsWithAlerts[position]
        holder.setToggle(currentStopWithAlerts.stop.stopId in enabledStops)
        holder.setOnEditButtonPressedListener {
            alertActions.onStopEditButtonPressed(currentStopWithAlerts.stop)
        }
        holder.setOnAlertEnabledAction {
            enabledStops.add(currentStopWithAlerts.stop.stopId)
            alertActions.onStopAlertsEnabled(currentStopWithAlerts)
        }
        holder.setOnAlertDisabledAction {
            enabledStops.remove(currentStopWithAlerts.stop.stopId)
            alertActions.onStopAlertsDisabled(currentStopWithAlerts)
        }
        holder.bind(stopsWithAlerts[position])
    }

    override fun onViewRecycled(holder: StopAlertsRecyclerViewHolder) {
        // When view is recycled clear all callbacks in order to avoid calling them when setToggle is called
        // by onBindViewHolder
        holder.resetCallbacks()
    }

    fun setStopsWithAlerts(newStopsWithAlerts: List<StopAlerts>){
        stopsWithAlerts = newStopsWithAlerts
        notifyDataSetChanged()
    }

    fun setEnabledStops(newEnabledStops: Set<Int>){
        enabledStops = newEnabledStops.toMutableSet()
        notifyDataSetChanged()
    }
}