package com.example.sheetcompute.ui.features

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.ActivityMainBinding
import com.example.sheetcompute.domain.di.MyTestClass
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var myTestClass: MyTestClass


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("HiltTest", myTestClass.sayHello())

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

        R.id.action_settings -> {
            val currentDest = navController.currentDestination?.id
            if (currentDest == R.id.settingFragment) {
                navController.popBackStack()
            } else {
                navController.navigate(R.id.settingFragment)
            }
            true
        }

        else -> false
    }
}

}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}