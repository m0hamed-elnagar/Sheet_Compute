package com.example.sheetcompute.ui.EmployeeAttendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.databinding.EmployeeAttendanceFragmentBinding
import com.example.sheetcompute.ui.utils.DateFilterHandler

class EmployeeAttendanceFragment : Fragment(){
private var _binding: EmployeeAttendanceFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmployeeAttendanceViewModel by viewModels()
    private lateinit var adapter: EmployeeAttendanceRecyclerView
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
//        setupToggleButtons()
//        observeData()
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
     private fun setupRecyclerView() {
        adapter = EmployeeAttendanceRecyclerView( requireContext()) { recordId ->

            // todo open edit dialog for the item
        }


        binding.rvEmployeeLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployeeLogs.setHasFixedSize(true)
        binding.rvEmployeeLogs.adapter = adapter
    }
     override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()
        _binding = null
    }
}