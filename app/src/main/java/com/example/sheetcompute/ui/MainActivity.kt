package com.example.sheetcompute.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.ActivityMainBinding
import com.example.sheetcompute.ui.features.attendanceHistory.pager.AttendanceHistoryPagerContainer
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupToolbarMenu()
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

private fun setupToolbarMenu() {
//    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)



    // Handle menu actions
   binding.toolbar.setOnMenuItemClickListener { item ->
    when (item.itemId) {
        R.id.action_calendar -> {
            val currentDest = navController.currentDestination?.id
            if (currentDest == R.id.holidaysCalendarFragment) {
                navController.popBackStack()
            } else {
                navController.navigate(R.id.holidaysCalendarFragment)
            }
            true
        }

//        R.id.action_settings -> {
//            val currentDest = navController.currentDestination?.id
//            if (currentDest == R.id.settingsFragment) {
//                navController.popBackStack()
//            } else {
//                navController.navigate(R.id.settingsFragment)
//            }
//            true
//        }

        else -> false
    }
}

}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
