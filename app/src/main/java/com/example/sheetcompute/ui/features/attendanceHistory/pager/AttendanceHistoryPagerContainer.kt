package com.example.sheetcompute.ui.features.attendanceHistory.pager


import android.os.Bundle
import android.os.Trace.isEnabled
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentAttendanceHistoryPagerContainerBinding
import androidx.core.view.get

class AttendanceHistoryPagerContainer : Fragment() {

    private var _binding: FragmentAttendanceHistoryPagerContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceHistoryPagerContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
//        setupBackNavigation()
    }

    private fun setupViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter =
            AttendancePagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.offscreenPageLimit = 2

        binding.filterBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    viewPager.currentItem = 0
                    true
                }

                R.id.nav_filter -> {
                    viewPager.currentItem = 1
                    true
                }

                else -> false
            }
        }

// Handle swipes updating bottom nav
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.filterBottomNav.menu[position].isChecked = true
            }
        })
    }
//private fun setupBackNavigation() {
//    val callback = object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            val viewPager = binding.viewPager
//            val currentItem = viewPager.currentItem
//
//            val pagerAdapter = binding.viewPager.adapter as? AttendancePagerAdapter
//            val navHostFragments = pagerAdapter?.getNavHostFragments()
//            val navHostFragment = navHostFragments?.getOrNull(currentItem)
//            val navController = navHostFragment?.navController
//
//            // Try to pop from stack first
//            if (navController?.popBackStack() == true) return
//
//            // If can't pop and not at position 0, go to tab 0
//            if (currentItem != 0) {
//                viewPager.currentItem = 0
//                binding.filterBottomNav.menu[0].isChecked = true
//                return
//            }
//
//            // Already at tab 0 with empty stack → exit
//            remove() // Prevent infinite loop
//            requireActivity().onBackPressedDispatcher.onBackPressed()
//        }
//    }
//
//    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}