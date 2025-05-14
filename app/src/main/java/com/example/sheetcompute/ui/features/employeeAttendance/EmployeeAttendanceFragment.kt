package com.example.sheetcompute.ui.features.employeeAttendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.data.roomDB.entities.AttendanceStatus
import com.example.sheetcompute.databinding.EmployeeAttendanceFragmentBinding
import com.example.sheetcompute.ui.subFeatures.utils.DateFilterHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmployeeAttendanceFragment : Fragment() {
    private var _binding: EmployeeAttendanceFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeAttendanceViewModel by viewModels()
    private lateinit var adapter: EmployeeAttendanceAdapter
    private lateinit var dateFilterHandler: DateFilterHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmployeeAttendanceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupDateFilterHandler()
        observeData()
        observeCounters()
        setupCounterClickListeners()
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
            }
        )
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAttendanceAdapter(requireContext()) { recordId ->
            // Open edit dialog for the item
            openEditDialog(recordId)
        }

        binding.rvEmployeeLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployeeLogs.setHasFixedSize(true)
        binding.rvEmployeeLogs.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredRecords.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isEmpty.collect { isEmpty ->
                    binding.txtEmptyLogs.visibility = if (isEmpty) View.VISIBLE else View.GONE
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbHistory.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeCounters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.presentCount.collect { count ->
                    binding.txtDaysWorked.text = count.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.absentCount.collect { count ->
                    binding.txtAbsentDays.text = count.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.extraDaysCount.collect { count ->
                    binding.txtExtraDays.text = count.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tardiesCount.collect { hours ->
                    binding.txtTardies.text = " $hours"

                }
            }
        }
    }

    private fun setupCounterClickListeners() {
        binding.presentCard.setOnClickListener {
            viewModel.setFilterByStatus(AttendanceStatus.PRESENT)
        }

        binding.absentCard.setOnClickListener {
            viewModel.setFilterByStatus(AttendanceStatus.ABSENT)
        }

        binding.extraDaysCard.setOnClickListener {
            viewModel.setFilterByStatus(AttendanceStatus.EXTRA_DAY)
        }

        binding.tardiesCard.setOnClickListener {
            viewModel.setFilterByStatus(AttendanceStatus.LATE)
        }
    }

    private fun openEditDialog(recordId: Int) {
        // Implement the logic to open an edit dialog for the selected record
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()
        _binding = null
    }
}