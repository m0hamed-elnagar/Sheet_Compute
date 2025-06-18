package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentDateFilterBinding
import com.example.sheetcompute.domain.PreferencesGateway
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.domain.excel.FilePickerFragmentHelper
import com.example.sheetcompute.domain.repo.AttendanceRepo
import com.example.sheetcompute.domain.repo.EmployeeRepo
import com.example.sheetcompute.ui.features.attendanceHistory.AttendanceAdapter
import com.example.sheetcompute.ui.subFeatures.spinners.DateFilterHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DateFilterFragment : Fragment() {
    private var _binding: FragmentDateFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DateFilterViewModel by viewModels()
    private lateinit var adapter: AttendanceAdapter
    private lateinit var dateFilterHandler: DateFilterHandler
    private lateinit var filePickerHelper: FilePickerFragmentHelper
    private val employeeRepo by lazy { EmployeeRepo() }
    private val attendanceRepo by lazy { AttendanceRepo() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateFilterBinding.inflate(inflater, container, false)
        filePickerHelper = FilePickerFragmentHelper(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupDateFilterHandler()
        observeData()
        binding.importSheet.setOnClickListener {
            filePickerHelper.pickExcelFile(
                onFilePicked = { inputStream ->
                    // Launch your import logic
                    lifecycleScope.launch {
                        val result = ExcelImporter.import(
                            inputStream,
                            PreferencesGateway.getWorkStartTime(),
                            employeeRepo,
                            attendanceRepo
                        )
                        Toast.makeText(
                            requireContext(),
                            "Imported: ${result.recordsAdded} records and ${result.newEmployees} new employee",
                            Toast.LENGTH_LONG
                        ).show()
                        delay(3000)
                        // Trigger data refresh after successful import
                        viewModel.refreshData()
                    }
                },
                onError = { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to import file: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

        }
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter() { employeeId ->
            val bundle = Bundle().apply {
                putInt("employeeId", employeeId)
            }
            findNavController().navigate(
                R.id.action_pagerContainerFragment_to_employeeAttendanceFragment,
                bundle
            )
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@DateFilterFragment.adapter
        }
    }

    private fun setupDateFilterHandler() {
        dateFilterHandler = DateFilterHandler(
            yearSpinner = binding.spinnerYear,
            monthSpinner = binding.spinnerMonth,
            includeAllMonths = false,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            onYearSelected = { year ->
                viewModel.setSelectedYear(year)
            },
            onMonthSelected = { month ->
                viewModel.setSelectedMonth(month)
            }
        )
    }


    private fun observeData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.attendanceRecords.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmpty.collect { isEmpty ->
                _binding?.txtEmptyHistory?.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                _binding?.pbHistory?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()
        _binding = null
    }
}