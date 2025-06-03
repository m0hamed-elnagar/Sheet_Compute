package com.example.sheetcompute.ui.features.attendanceHistory.pager


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}