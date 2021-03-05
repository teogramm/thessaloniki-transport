package xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewRouteDetailsStopBinding

class StopListRecyclerViewAdapter(private val stops: List<Stop>):
    RecyclerView.Adapter<StopListRecyclerViewAdapter.StopViewHolder>() {

    class StopViewHolder(binding: RecyclerviewRouteDetailsStopBinding): RecyclerView.ViewHolder(binding.root){
        private val stopIndexTextView = binding.sequence
        private val stopNameTextView = binding.stopName

        fun bindView(stop: Stop, index: Int) {
            stopNameTextView.text = stop.nameEL
            stopIndexTextView.text = index.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val stopBinding = RecyclerviewRouteDetailsStopBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StopViewHolder(stopBinding)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        // Position is 0-indexed. Display should be 1-indexed.
        holder.bindView(stops[position], position + 1)
    }

    override fun getItemCount(): Int {
        return stops.size
    }
}