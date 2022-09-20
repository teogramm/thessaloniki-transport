package xyz.teogramm.thessalonikitransit.fragments.lineDisplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.LineWithRoutes
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewDisplayLineBinding

/**
 * Displays a line and its routes.
 */
class LineRecyclerViewAdapter(private val linesWithRoutes: List<LineWithRoutes>):
    RecyclerView.Adapter<LineRecyclerViewAdapter.LineViewHolder>() {

    /**
     * Set keeping line numbers of all [LineViewHolder]s that the user has expanded. When the routes of a line are
     * expanded, the number of the line is put in the Set and it is removed if the user toggles the routes again.
     * When a holder is created, the route RecyclerView is set to open or closed, depending on whether the line id
     * for the line is in this set.
     */
    private val openedLinesPositions = HashSet<String>()

    class LineViewHolder(binding: RecyclerviewDisplayLineBinding): RecyclerView.ViewHolder(binding.root) {
        private val numberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val nestedRecyclerView = binding.routeRecyclerView

        /**
         * @param routesVisible If true the route RecyclerView is set to visible, else is set to gone.
         */
        fun bind(lineWithRoutes: LineWithRoutes, routesVisible: Boolean) {
            numberTextView.text = lineWithRoutes.line.number
            lineNameTextView.text = lineWithRoutes.line.nameEL

            if(routesVisible) {
                nestedRecyclerView.visibility = View.VISIBLE
            } else{
                nestedRecyclerView.visibility = View.GONE
            }
            nestedRecyclerView.layoutManager = LinearLayoutManager(itemView.context,RecyclerView.VERTICAL,false)
            nestedRecyclerView.adapter = RouteRecyclerViewAdapter(lineWithRoutes.routes, lineWithRoutes.line)
        }

        /**
         * Toggles visibility of RecyclerView containing
         */
        fun toggleRoutesVisibility() {
            if(nestedRecyclerView.visibility == View.VISIBLE) {
                nestedRecyclerView.visibility = View.GONE
            } else{
                nestedRecyclerView.visibility = View.VISIBLE
            }
        }

        /**
         * Sets the route RecyclerView to the [View.GONE] state.
         */
        fun hideRoutes() {
            nestedRecyclerView.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val lineBinding = RecyclerviewDisplayLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineViewHolder(lineBinding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val lineNumber = linesWithRoutes[position].line.number
        holder.itemView.setOnClickListener {
            holder.toggleRoutesVisibility()
            if( lineNumber in openedLinesPositions) {
                openedLinesPositions.remove(lineNumber)
            } else {
                openedLinesPositions.add(lineNumber)
            }
            notifyItemChanged(position)
        }
        holder.bind(linesWithRoutes[position], lineNumber in openedLinesPositions)
    }

    override fun onViewRecycled(holder: LineViewHolder) {
        super.onViewRecycled(holder)
        // When a holder is recycled we want the recycler view reset to the GONE state, it will be opened/closed
        // in the onBindView function
        holder.hideRoutes()
    }

    override fun getItemCount(): Int {
        return linesWithRoutes.size
    }
}