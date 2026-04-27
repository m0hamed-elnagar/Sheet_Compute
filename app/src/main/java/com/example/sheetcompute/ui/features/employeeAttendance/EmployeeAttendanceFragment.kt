package com.example.sheetcompute.ui.features.employeeAttendance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.databinding.EmployeeAttendanceFragmentBinding
import com.example.sheetcompute.domain.excel.export.EmployeeRecordsWorkbookBuilder
import com.example.sheetcompute.ui.subFeatures.dialogs.DatePickerDialogs
import com.example.sheetcompute.ui.subFeatures.sheetPicker.FilePickerFragmentHelper
import com.example.sheetcompute.ui.subFeatures.spinners.DateFilterHandler
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatDateRange
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatMinutesToHoursMinutes
import com.example.sheetcompute.ui.subFeatures.utils.ExcelFileSaver.saveToDownloads
import com.example.sheetcompute.ui.subFeatures.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class EmployeeAttendanceFragment : Fragment() {
    private var _binding: EmployeeAttendanceFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeAttendanceViewModel by viewModels()
    private lateinit var adapter: EmployeeAttendanceAdapter
    private lateinit var dateFilterHandler: DateFilterHandler
    private val args by navArgs<EmployeeAttendanceFragmentArgs>()
    private lateinit var filePicker: FilePickerFragmentHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmployeeAttendanceFragmentBinding.inflate(inflater, container, false)
        filePicker = FilePickerFragmentHelper(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        if (args.employeeId == 0L) {
            Log.e("EmployeeAttendanceFragment", "Invalid or missing employeeId argument! Navigation will not proceed.")
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }
        viewModel.setEmployeeId(args.employeeId)
        setupObservers()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupDateControls()
        setupCounterClickListeners()
        binding.exportReport.setOnClickListener { exportEmployeeRecordsToXls() }
        binding.ToWhatsApp.setOnClickListener { shareEmployeeRecordsToWhatsapp() }
    }

    private fun exportEmployeeRecordsToXls() {
        val employee = viewModel.selectedEmployee.value
        val records = viewModel.filteredRecords.value
        if (employee != null && records.isNotEmpty()) {
            val workbook = EmployeeRecordsWorkbookBuilder.buildWorkbook(
                listOf(employee), records
            )
            val file = saveToDownloads(requireContext(), workbook,"Employees Records")
            if (file != null) {
                showToast(requireContext(), "Exported to: ${file.name}")
            } else {
               showToast(requireContext(), "Failed to export XLS")
            }
        } else {
           showToast(requireContext(), "No data to export")
        }
    }

    private fun shareEmployeeRecordsToWhatsapp() {
        val employee = viewModel.selectedEmployee.value
        val records = viewModel.filteredRecords.value
        if (employee != null && records.isNotEmpty()) {
            val workbook = EmployeeRecordsWorkbookBuilder.buildWorkbook(
                listOf(employee), records
            )
            val file = saveToDownloads(requireContext(), workbook,"Employees Records")
            if (file != null) {
                com.example.sheetcompute.ui.subFeatures.utils.shareXlsViaWhatsApp(requireContext(), file)
            } else {
                showToast(requireContext(), "Failed to export XLS")
            }
        } else {
            showToast(requireContext(), "No data to export")
        }
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAttendanceAdapter(requireContext()) { recordId ->
            openEditDialog(recordId)
        }
        binding.rvEmployeeLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EmployeeAttendanceFragment.adapter
        }
    }

    private fun setupDateControls() {
        dateFilterHandler = DateFilterHandler(
            yearSpinner = binding.spinnerYear,
            monthSpinner = binding.spinnerMonth,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            onYearSelected = { year ->
                year?.let { y ->
                    viewModel.setSelectedYear(y)
                }
            },
            onMonthSelected = { month ->
                val year = binding.spinnerYear.selectedItem?.toString()?.toIntOrNull()
                val monthToUse = month?.plus(1) ?: 0
                year?.let { y -> viewModel.setMonthRange(monthToUse, y) }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableYears.collect { years ->
                dateFilterHandler.updateYears(years)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableMonthsForSelectedYear.collect { months ->
                dateFilterHandler.updateMonths(months)
            }
        }

        binding.btnDateRange.setOnClickListener {
            DatePickerDialogs.showRangePickerDialog(
                requireActivity().supportFragmentManager
            ) { startDate, endDate ->
                viewModel.setCustomRange(startDate, endDate)
                binding.txtDateRange.text = formatDateRange(startDate, endDate)
                binding.dateRangeDisplay.visibility = View.VISIBLE
                binding.dateSelectors.visibility = View.GONE
            }
        }

        binding.btnClearRange.setOnClickListener {
            viewModel.clearFilters()
            binding.dateRangeDisplay.visibility = View.GONE
            binding.dateSelectors.visibility = View.VISIBLE
            dateFilterHandler.setSelections(null, null)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredRecords.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedEmployee.collectLatest { emp ->
                    binding.txtEmployeeName.text = emp?.name ?: "Unknown Employee"
                    binding.txtEmployeeId.text = emp?.id?.toString() ?: ""
                    
                    val namePart = emp?.name?.take(1) ?: "E"
                    val idPart = emp?.id?.toString()?.take(1) ?: ""
                    binding.txtEmployeeBadge.text = "$namePart$idPart".uppercase(Locale.ROOT)
                }
            }
        }
        //todo when nothing pressed show all
        viewModel.presentCount.observe(viewLifecycleOwner) { count ->
            binding.txtDaysWorked.text = count.toString()
        }
        viewModel.absentCount.observe(viewLifecycleOwner) { count ->
            binding.txtAbsentDays.text = count.toString()
        }
        viewModel.extraDaysCount.observe(viewLifecycleOwner) { count ->
            binding.txtExtraDays.text = count.toString()
        }
        viewModel.tardiesCount.observe(viewLifecycleOwner) { minutes ->
            binding.txtTardies.text = formatMinutesToHoursMinutes(minutes)
        }

        viewModel.selectedStatuses.observe(viewLifecycleOwner) { statuses ->
            binding.presentCard.isSelected = statuses.contains(AttendanceStatus.PRESENT)
            binding.absentCard.isSelected = statuses.contains(AttendanceStatus.ABSENT)
            binding.extraDaysCard.isSelected = statuses.contains(AttendanceStatus.EXTRA_DAY)
            binding.tardiesCard.isSelected = statuses.contains(AttendanceStatus.LATE)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            _binding?.txtEmptyLogs?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun setupCounterClickListeners() {
        binding.presentCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.PRESENT)
        }

        binding.absentCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.ABSENT)
        }

        binding.extraDaysCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.EXTRA_DAY)
        }

        binding.tardiesCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.LATE)
        }
    }

    private fun openEditDialog(recordId: Long) {
        // Implement edit dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()
        _binding = null
    }
}