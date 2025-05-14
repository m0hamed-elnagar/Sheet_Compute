package com.example.sheetcompute.ui.features.attendanceHistory.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sheetcompute.R
import com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory.DateFilterFragment
import com.example.sheetcompute.ui.features.attendanceHistory.searchHistory.SearchHistoryFragment

class AttendancePagerAdapter ( fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf(
        SearchHistoryFragment(),
        DateFilterFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}