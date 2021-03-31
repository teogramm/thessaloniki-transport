package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewStopLineArrivalBinding
import xyz.teogramm.thessalonikitransit.fragments.lineDisplay.LineDisplayFragment
import xyz.teogramm.thessalonikitransit.viewModels.RouteViewModel

class ArrivalTimesRecyclerViewAdapter(private val lines: List<Line>):
        RecyclerView.Adapter<ArrivalTimesRecyclerViewAdapter.LineArrivalTimesViewHolder>() {
    class LineArrivalTimesViewHolder(binding: RecyclerviewStopLineArrivalBinding):RecyclerView.ViewHolder(binding.root) {
        private val lineNumberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val arrivalTimeTextView = binding.arrivalTime

        fun bindView(line: Line) {
            lineNumberTextView.text = line.number
            lineNameTextView.text = line.nameEL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineArrivalTimesViewHolder {
        val binding = RecyclerviewStopLineArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineArrivalTimesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int) {
        holder.bindView(lines[position])
    }

    override fun getItemCount(): Int {
        return lines.size
    }
}