package com.example.sheetcompute.ui.attendanceHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentAttendanceHistoryBinding
import com.example.sheetcompute.ui.utils.DateFilterHandler
import com.example.sheetcompute.ui.utils.scrollToTop
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AttendanceHistoryFragment : Fragment() {
    private var _binding: FragmentAttendanceHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var adapter: AttendanceRecyclerView
    private var searchJob: Job? = null
    private lateinit var dateFilterHandler: DateFilterHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupDateFilterHandler()
        setupToggleButtons()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = AttendanceRecyclerView(viewLifecycleOwner.lifecycleScope) { employeeId ->
            val bundle = bundleOf("employeeId" to employeeId)
            findNavController().navigate(
                R.id.action_attendanceHistoryFragment_to_fragmentEmployeeAttendance,
                bundle
            )
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@AttendanceHistoryFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.orEmpty()
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    viewModel.setSearchQuery(query)
                }
                return true
            }
        })
    }

    private fun setupDateFilterHandler() {
        dateFilterHandler = DateFilterHandler(
            yearSpinner = binding.spinnerYear,
            monthSpinner = binding.spinnerMonth,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            onYearSelected = { year ->
                viewModel.setSelectedYear(year)
            },
            onMonthSelected = { month ->
                viewModel.setSelectedMonth(month)
            })

    }

    private fun setupToggleButtons() {
        binding.filterBottomNav.selectedItemId = R.id.nav_search
        binding.filterBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    viewModel.switchToSearchView()
                    showSearchView()
                    true
                }

                R.id.nav_filter -> {
                    viewModel.switchToMonthView()
                    showDateFilters()
                    true
                }

                else -> false
            }
        }
    }

    private fun showDateFilters() {
        binding.dateSelectors.visibility = View.VISIBLE
        binding.searchRow.visibility = View.GONE
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }

    private fun showSearchView() {
        binding.searchRow.visibility = View.VISIBLE
        binding.dateSelectors.visibility = View.GONE
        binding.searchView.requestFocus()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.attendanceRecords.collectLatest { pagingData ->
                adapter.submitData(pagingData)
                binding.rvHistory.postDelayed({
                    binding.rvHistory.scrollToTop()
                }, 100)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmpty.collect { isEmpty ->
                binding.txtEmptyHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.pbHistory.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        dateFilterHandler.cleanup()
        _binding = null
    }
}