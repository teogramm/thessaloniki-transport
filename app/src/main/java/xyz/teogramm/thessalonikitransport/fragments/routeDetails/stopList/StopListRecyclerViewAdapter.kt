package xyz.teogramm.thessalonikitransport.fragments.routeDetails.stopList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransport.R
import xyz.teogramm.thessalonikitransport.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransport.databinding.RecyclerviewRouteDetailsStopBinding
import xyz.teogramm.thessalonikitransport.viewModels.StopViewModel

class StopListRecyclerViewAdapter(stops: List<Stop>):
    RecyclerView.Adapter<StopListRecyclerViewAdapter.StopViewHolder>() {

    private val stops = stops.toMutableList()

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

        holder.itemView.setOnClickListener {
            val currentFragment  = FragmentManager.findFragment<StopListFragment>(holder.itemView)
            val stopViewModel: StopViewModel by currentFragment.activityViewModels()
            stopViewModel.setStop(stops[position])
            currentFragment.findNavController().navigate(R.id.action_routeDetailsFragment_to_stopDetailsFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStops(newStops: List<Stop> ){
        stops.clear()
        stops.addAll(newStops)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return stops.size
    }
}