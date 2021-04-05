package xyz.teogramm.thessalonikitransit.fragments.stopDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.databinding.RecyclerviewStopLineArrivalBinding
import xyz.teogramm.thessalonikitransit.viewModels.LineWithArrivalTime

// TODO: Improve code quality, sort lines by arrival time.
class ArrivalTimesRecyclerViewAdapter(private var lines: List<LineWithArrivalTime>):
        RecyclerView.Adapter<ArrivalTimesRecyclerViewAdapter.LineArrivalTimesViewHolder>() {
    class LineArrivalTimesViewHolder(binding: RecyclerviewStopLineArrivalBinding):LifecycleViewHolder(binding.root) {
        private val lineNumberTextView = binding.lineNumber
        private val lineNameTextView = binding.lineName
        private val arrivalTimeTextView = binding.arrivalTime

        fun bindView(line: Line) {
            lineNumberTextView.text = line.number
            lineNameTextView.text = line.nameEL
        }

        fun setTime(arrivalTime: Int) {
            arrivalTimeTextView.text = "${arrivalTime}'"
        }
    }

    override fun onViewAttachedToWindow(holder: LineArrivalTimesViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    override fun onViewDetachedFromWindow(holder: LineArrivalTimesViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineArrivalTimesViewHolder {
        val binding = RecyclerviewStopLineArrivalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineArrivalTimesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineArrivalTimesViewHolder, position: Int) {
        holder.bindView(lines[position].line)
        lines[position].getArrivalTimes().observeForever {
            holder.setTime(it.first())
        }
    }

    override fun getItemCount(): Int {
        return lines.size
    }
}

abstract class LifecycleViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView), LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun onAppear() {
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun onDisappear() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}