package xyz.teogramm.thessalonikitransit.fragments.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.teogramm.thessalonikitransit.database.transit.entities.Line
import xyz.teogramm.thessalonikitransit.databinding.DialogAlertsRecyclerviewLineBinding

/**
 * Recycler View which displays lines and whether they are enabled or not
 */
class CreateAlertLineRecyclerViewAdapter(private val lines: List<Line>,
                                         private val enabled: MutableSet<Line>):
    RecyclerView.Adapter<CreateAlertLineRecyclerViewAdapter.LineViewHolder>() {
    class LineViewHolder(private val binding: DialogAlertsRecyclerviewLineBinding): RecyclerView.ViewHolder(binding.root){
        private val lineText = binding.alertLineName
        val alertEnabledCheckbox = binding.alertCheckBox

        fun bind(line: Line, enabled: Boolean){
            lineText.text = "${line.number} - ${line.nameEL}"
            alertEnabledCheckbox.isChecked = enabled
        }
    }

    /**
     * Return the lines for which alerts have been enabled.
     */
    fun getEnabled() = enabled.toSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val binding = DialogAlertsRecyclerviewLineBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return LineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val line = lines[position]
        holder.bind(line, enabled.contains(line))
        holder.alertEnabledCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                enabled.add(line)
            }else{
                enabled.remove(line)
            }
        }
    }
}