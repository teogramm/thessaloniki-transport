package xyz.teogramm.thessalonikitransit.recyclerViews.lines
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.LineWithRoutes
import xyz.teogramm.thessalonikitransit.databinding.FragmentLineDisplayLineBinding

/**
 * Displays a line and its routes.
 */
class LineRecyclerViewAdapter(private val linesWithRoutes: List<LineWithRoutes>):
    RecyclerView.Adapter<LineRecyclerViewAdapter.LineViewHolder>() {
    class LineViewHolder(binding: FragmentLineDisplayLineBinding): RecyclerView.ViewHolder(binding.root) {
        private val numberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val nestedRecyclerView = binding.routeRecyclerView

        fun bind(line: LineWithRoutes) {
            numberTextView.text = line.line.number
            lineNameTextView.text = line.line.nameEL

            nestedRecyclerView.layoutManager = LinearLayoutManager(itemView.context,LinearLayout.VERTICAL,false)
            nestedRecyclerView.adapter = RouteRecyclerViewAdapter(line.routes)
            // Don't know why this is required, but it doesn't show if you set it in the layout XML
            nestedRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val lineBinding = FragmentLineDisplayLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineViewHolder(lineBinding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            notifyItemChanged(position)
        }
        holder.bind(linesWithRoutes[position])
    }

    override fun getItemCount(): Int {
        return linesWithRoutes.size
    }
}