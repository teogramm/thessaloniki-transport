package xyz.teogramm.thessalonikitransit.fragments.routeDetails.stopList

import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewRouteDetailsStopBinding
import xyz.teogramm.thessalonikitransit.fragments.lineDisplay.LineDisplayFragment
import xyz.teogramm.thessalonikitransit.fragments.routeDetails.RouteDetailsFragment
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel
import xyz.teogramm.thessalonikitransit.viewModels.StopViewModel

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

        holder.itemView.setOnClickListener {
            val currentFragment  = FragmentManager.findFragment<StopListFragment>(holder.itemView)
            val stopViewModel: StopViewModel by currentFragment.activityViewModels()
            stopViewModel.setStop(stops[position])
            currentFragment.findNavController().navigate(R.id.action_routeDetailsFragment_to_stopDetailsFragment)
        }
    }

    override fun getItemCount(): Int {
        return stops.size
    }
}