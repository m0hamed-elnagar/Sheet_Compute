package com.example.sheetcompute.ui.attendanceHistory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentAttendanceHistoryBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AttendanceHistoryFragment : Fragment() {

    private var _binding: FragmentAttendanceHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var adapter: AttendanceRecyclerView
    private var searchJob: Job? = null
    private var yearSelectionJob: Job? = null
    private var monthSelectionJob: Job? = null

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
        setupDateFilters()
        setupToggleButtons()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = AttendanceRecyclerView(viewLifecycleOwner.lifecycleScope) { recordId ->
            val bundle = bundleOf("recordId" to recordId)
            // Navigate or handle click
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.adapter = adapter
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

    private fun setupDateFilters() {
        // Setup year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 10..currentYear).reversed().map { it.toString() }
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerYear.adapter = adapter
        }

        // Setup month spinner
        val months = listOf("All Months") + (0..11).map {
            Calendar.getInstance().apply { set(Calendar.MONTH, it) }.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.getDefault()
            ) ?: ""
        }
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMonth.adapter = adapter
            binding.spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH) + 1)
        }

    // Set default to current month
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    binding.spinnerMonth.setSelection(currentMonth + 1)

    // Year selection listener
    binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            yearSelectionJob?.cancel()
            yearSelectionJob = viewLifecycleOwner.lifecycleScope.launch {
                val year = parent.getItemAtPosition(position).toString().toIntOrNull()
                viewModel.setSelectedYear(year)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    // Month selection listener
    binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            monthSelectionJob?.cancel()
            monthSelectionJob = viewLifecycleOwner.lifecycleScope.launch {
                val month = if (position == 0) null else position - 1
                viewModel.setSelectedMonth(month)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}

    private fun setupToggleButtons() {
        binding.bySearch.isSelected = true
        binding.filterBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    showSearchView()
                    viewModel.switchToSearchView()
                    true
                }
                R.id.nav_filter -> {
                    showDateFilters()
                    viewModel.switchToMonthView()
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
        yearSelectionJob?.cancel()
        monthSelectionJob?.cancel()
        _binding = null
    }
}
