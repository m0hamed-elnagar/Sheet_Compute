package com.example.sheetcompute.ui.features.attendanceHistory.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory.DateFilterFragment
import com.example.sheetcompute.ui.features.attendanceHistory.searchHistory.SearchEmployeeFragment

class AttendancePagerAdapter ( fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf(
        SearchEmployeeFragment(),
        DateFilterFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}