package xyz.teogramm.thessalonikitransit.recyclerViews.lines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.database.transit.entities.RouteWithLastStop
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewDisplayLineRouteBinding
import xyz.teogramm.thessalonikitransit.fragments.LineDisplayFragment
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel

/**
 * Displays all routes for one line.
 * @param line Line the routes belong to.
 */
class RouteRecyclerViewAdapter(private val routes: List<RouteWithLastStop>, private val line: Line):
        RecyclerView.Adapter<RouteRecyclerViewAdapter.RouteViewHolder>() {
    class RouteViewHolder(private val binding: RecyclerviewDisplayLineRouteBinding): RecyclerView.ViewHolder(binding.root) {
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
        val binding = RecyclerviewDisplayLineRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bindView(routes[position])

        holder.itemView.setOnClickListener {
            val currentFragment  = FragmentManager.findFragment<LineDisplayFragment>(holder.itemView)
            val routeViewModel: RouteViewModel by currentFragment.activityViewModels()
            routeViewModel.setSelected(line, routes[position].route)
            currentFragment.findNavController().navigate(R.id.action_lineDisplayFragment_to_routeDetailsFragment)
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }
}