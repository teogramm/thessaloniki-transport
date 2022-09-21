package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewStopLineArrivalBinding

/**
 * @param arrivalTimes Maps each line id to its arrival time
 */
// TODO: Improve code quality, sort lines by arrival time.
class ArrivalTimesRecyclerViewAdapter(private var lines: List<Line>):
        RecyclerView.Adapter<ArrivalTimesRecyclerViewAdapter.LineArrivalTimesViewHolder>() {

    private var arrivalTimes = emptyMap<Int,Int>()
    private val lineIdToViewHolderPosition = hashMapOf<Int, Int>()

    class LineArrivalTimesViewHolder(binding: RecyclerviewStopLineArrivalBinding): RecyclerView.ViewHolder(binding.root) {
        private val lineNumberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val arrivalTimeTextView = binding.arrivalTime

        fun bindView(line: Line, arrivalTime: Int?) {
            lineNumberTextView.text = line.number
            lineNameTextView.text = line.nameEL
            arrivalTime?.let { setTime(arrivalTime) }
        }

        fun setTime(arrivalTime: Int) {
            arrivalTimeTextView.text = "$arrivalTime"
        }

        fun resetTime() {
            arrivalTimeTextView.text = ""
        }
    }

    fun updateArrivalTimes(times: Map<Int,Int>){
        arrivalTimes = times
        times.forEach{ (lineId, _) ->
            // If a line is not visible it might not be in the map
            lineIdToViewHolderPosition[lineId]?.let { notifyItemChanged(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineArrivalTimesViewHolder {
        val binding = RecyclerviewStopLineArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineArrivalTimesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int) {
        val lineId = lines[position].lineId
        lineIdToViewHolderPosition[lineId] = holder.adapterPosition
        // Check if time exists for this line
        arrivalTimes[lineId]?.let { holder.setTime(it) }
        holder.bindView(lines[position], arrivalTimes[lineId])
    }

    override fun onViewRecycled(holder: LineArrivalTimesViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return lines.size
    }
}