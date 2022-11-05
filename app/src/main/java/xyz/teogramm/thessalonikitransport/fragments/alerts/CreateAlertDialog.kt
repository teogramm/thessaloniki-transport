package xyz.teogramm.thessalonikitransport.fragments.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teogramm.thessalonikitransport.databinding.DialogAlertsCreateBinding
import xyz.teogramm.thessalonikitransport.viewModels.StopViewModel

class CreateAlertDialog: DialogFragment() {

    private val stopViewModel: StopViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogAlertsCreateBinding.inflate(layoutInflater, null, false)

        binding.notificationMinutesInput.doOnTextChanged { text, _, _, _ ->
            // Text is valid if it is a number >= 1
            var valid = !text.isNullOrBlank()
            valid = try {
                val value = text.toString().toInt()
                valid && value > 1
            } catch (e: NumberFormatException){
                false
            }
            if (!valid) {
                binding.notificationMinutesInput.error = "Enter a positive number"
                binding.okButton.isEnabled = false
            } else {
                binding.notificationMinutesInput.error = null
                binding.okButton.isEnabled = true
            }
        }

        binding.alertRoutesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                stopViewModel.alertDialogUiState.collectLatest { uiState ->
                    uiState?.let {
                        // TODO: Maybe don't recreate the adapter but replace its data instead
                        if(it.notificationTimeMinutes == null){
                            binding.okButton.isEnabled = false
                            binding.notificationMinutesInput.setText("")
                            // Remove the error initially
                            binding.notificationMinutesInput.error = null
                        }else{
                            binding.notificationMinutesInput.setText(it.notificationTimeMinutes.toString())
                        }
                        binding.alertRoutesRecyclerView.adapter =
                            CreateAlertLineRecyclerViewAdapter(it.lines, it.enabled.toMutableSet())
                    }
                }
            }
        }

        binding.cancelButton.setOnClickListener{
            this.dismiss()
        }

        binding.deleteButton.setOnClickListener {
            stopViewModel.deleteStopAlerts()
            this.dismiss()
        }

        binding.okButton.setOnClickListener {
            val enabledAlerts = (binding.alertRoutesRecyclerView.adapter as CreateAlertLineRecyclerViewAdapter)
                .getEnabled()
            val notificationThreshold = binding.notificationMinutesInput.text.toString().toInt()
            stopViewModel.setStopAlerts(enabledAlerts, notificationThreshold)
            this.dismiss()
        }

        return binding.root
    }
}