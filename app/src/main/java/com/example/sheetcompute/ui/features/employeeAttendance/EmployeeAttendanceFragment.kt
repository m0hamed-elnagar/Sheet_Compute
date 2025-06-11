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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.databinding.EmployeeAttendanceFragmentBinding
import com.example.sheetcompute.ui.subFeatures.spinners.DateFilterHandler
import com.example.sheetcompute.ui.subFeatures.dialogs.DatePickerUtils
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatDateRange
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
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupDateControls()
        setupCounterClickListeners()
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
                year?.let {
                    val month = binding.spinnerMonth.selectedItemPosition
                    Log.d("DateFilterHandler", "setupDateControls: $month")
                    viewModel.setMonthRange(month, year)

                }
            },
            onMonthSelected = { month ->
                val year = binding.spinnerYear.selectedItem.toString().toIntOrNull()

                month?.let {
                    year?.let { viewModel.setMonthRange(month, it) }
                } ?: year?.let { viewModel.setMonthRange(0, it) }

            }
        )

        binding.btnDateRange.setOnClickListener {
            DatePickerUtils.showRangePickerDialog(
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
                    adapter.submitData(it)
                }
            }
        }

        viewModel.presentCount.observe(viewLifecycleOwner) { count ->
            _binding?.txtDaysWorked?.text = count.toString()
            _binding?.presentCard?.isSelected = viewModel.isStatusSelected(AttendanceStatus.PRESENT)
        }

        viewModel.absentCount.observe(viewLifecycleOwner) { count ->
            _binding?.txtAbsentDays?.text = count.toString()
            _binding?.absentCard?.isSelected = viewModel.isStatusSelected(AttendanceStatus.ABSENT)
        }

        viewModel.extraDaysCount.observe(viewLifecycleOwner) { count ->
            _binding?.txtExtraDays?.text = count.toString()
            _binding?.extraDaysCard?.isSelected =
                viewModel.isStatusSelected(AttendanceStatus.EXTRA_DAY)
        }

        viewModel.tardiesCount.observe(viewLifecycleOwner) { hours ->
            _binding?.txtTardies?.text = hours.toString()
            _binding?.tardiesCard?.isSelected =
                             viewModel.isStatusSelected(AttendanceStatus.LATE)

        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            _binding?.txtEmptyLogs?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun setupCounterClickListeners() {
        binding.presentCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.PRESENT)
            animateCardSelection(binding.presentCard, AttendanceStatus.PRESENT)
        }

        binding.absentCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.ABSENT)
            animateCardSelection(binding.absentCard, AttendanceStatus.ABSENT)
        }

        binding.extraDaysCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.EXTRA_DAY)
            animateCardSelection(binding.extraDaysCard, AttendanceStatus.EXTRA_DAY)
        }

        binding.tardiesCard.setOnClickListener {
            viewModel.toggleStatusFilter(AttendanceStatus.LATE)
            animateCardSelection(binding.tardiesCard, AttendanceStatus.LATE)
        }
    }

    private fun animateCardSelection(card: View, status: AttendanceStatus) {
        val isSelected = viewModel.isStatusSelected(status)

        val selectedScale = if (isSelected) .95f else 1f
        val selectedElevation = if (isSelected) 8f else 4f

        // Animate scale and elevation
        card.animate()
            .scaleX(selectedScale)
            .scaleY(selectedScale)
            .setDuration(150)
            .withStartAction {
                card.animate()
                    .translationZ(selectedElevation)
                    .setDuration(150)
                    .start()
            }
            .withEndAction {
                card.isSelected = isSelected
            }
            .start()



}


    private fun openEditDialog(recordId: Int) {
        // Implement edit dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()
        _binding = null
    }
}