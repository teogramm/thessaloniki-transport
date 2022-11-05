package xyz.teogramm.thessalonikitransport

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import xyz.teogramm.thessalonikitransport.database.transit.DatabaseInitializer

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_graph)
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.onboardingFragment, R.id.routeDetailsFragment, R.id.stopDetailsFragment -> hideBottomNavigation()
                else-> showBottomNavigation()
            }
        }
        if(!DatabaseInitializer.isDbInitialized(applicationContext)) {
            navGraph.setStartDestination(R.id.onboardingFragment)
            navController.graph = navGraph
        }
        NavigationUI.setupWithNavController(bottomNavigationView,navController)
    }

    private fun hideBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
    }

    private fun showBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }
}
