package xyz.teogramm.thessalonikitransit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.teogramm.thessalonikitransit.R
import xyz.teogramm.thessalonikitransit.database.transit.DatabaseInitializer
import xyz.teogramm.thessalonikitransit.databinding.FragmentOnboardingBinding
import xyz.teogramm.thessalonikitransit.repositories.StaticDataRepository
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    @Inject lateinit var staticRepository: StaticDataRepository

    private var _binding: FragmentOnboardingBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Set up ViewBinding
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root
        if (container != null) {
            // Keep screen on while DB initializes
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            // Mark the DB as uninitialized
            DatabaseInitializer.setDbInitialized(context, false)
            // Import all the data
            val downloadJob = this.lifecycleScope.launch {
                staticRepository.downloadAndInitializeDB()
            }
            // When the DB population finishes, change the UI
            downloadJob.invokeOnCompletion {
                lifecycleScope.launch {
                    initComplete()
                }
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Updates the UI when database initialization is complete
     */
    private suspend fun initComplete() {
        withContext(Dispatchers.Main) {
            // Set DB initialized
            DatabaseInitializer.setDbInitialized(context)
            // Remove flag to keep screen on
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            // Navigate to home fragment
            findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment)
        }
    }
}
