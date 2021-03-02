package xyz.teogramm.thessalonikitransit.recyclerViews.lines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.Route
import xyz.teogramm.thessalonikitransit.database.transit.entities.RouteWithLastStop
import xyz.teogramm.thessalonikitransit.database.transit.entities.Stop
import xyz.teogramm.thessalonikitransit.databinding.FragmentLineDisplayRouteBinding
import xyz.teogramm.thessalonikitransit.viewModels.LinesRoutesViewModel

class RouteRecyclerViewAdapter(private val routes: List<RouteWithLastStop>):
        RecyclerView.Adapter<RouteRecyclerViewAdapter.RouteViewHolder>() {
    class RouteViewHolder(private val binding: FragmentLineDisplayRouteBinding): RecyclerView.ViewHolder(binding.root) {
        private val officialTitleTextView = binding.officialTitle
        private val directionTextView = binding.direction

        fun bindView(route: RouteWithLastStop){
            officialTitleTextView.text = route.route.nameEL
            directionTextView.text = itemView.context.getString(R.string.towards_with_placeholder,
                route.lastStop.nameEL
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = FragmentLineDisplayRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bindView(routes[position])
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}