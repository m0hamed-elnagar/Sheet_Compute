package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentDateFilterBinding
import com.example.sheetcompute.ui.features.attendanceHistory.AttendanceAdapter
import com.example.sheetcompute.ui.subFeatures.utils.DateFilterHandler
import kotlinx.coroutines.launch

class DateFilterFragment : Fragment() {
    private var _binding: FragmentDateFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DateFilterViewModel by viewModels()
    private lateinit var adapter: AttendanceAdapter
    private lateinit var dateFilterHandler: DateFilterHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupDateFilterHandler()
        observeData()
        binding.btnDateRange.setOnClickListener {

        }
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(viewLifecycleOwner.lifecycleScope) { employeeId ->
            val bundle = Bundle().apply {
                putInt("employeeId", employeeId)
            }
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(
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
                binding.txtEmptyHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                binding.pbHistory.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dateFilterHandler.cleanup()

        _binding = null
    }
}