package com.example.sheetcompute.ui.features.attendanceHistory.dateFilterHistory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentDateFilterBinding
import com.example.sheetcompute.ui.subFeatures.dialogs.ImportConfirmationDialog
import com.example.sheetcompute.ui.subFeatures.sheetPicker.FilePickerFragmentHelper
import com.example.sheetcompute.ui.subFeatures.spinners.DateFilterHandler
import com.example.sheetcompute.ui.subFeatures.utils.isInternetAvailable
import com.example.sheetcompute.ui.subFeatures.utils.showToast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.launch

class DateFilterFragment : Fragment() {
    private var _binding: FragmentDateFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DateFilterViewModel by viewModels()
    private lateinit var adapter: AttendanceSummaryAdapter
    private lateinit var dateFilterHandler: DateFilterHandler
    private lateinit var filePickerHelper: FilePickerFragmentHelper
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
        binding.importSheet.setOnClickListener { showImportDialog() }
    }

    private fun showImportDialog() {
        ImportConfirmationDialog(requireContext(), onConfirm = {
            extractExcel()
        }).show()
    }

    private fun extractExcel() {
        if (isInternetAvailable(requireContext())) {
            val isExcelEnabled = Firebase.remoteConfig.getBoolean("excel_enabled")

            if (isExcelEnabled) {
                showFilePicker()
            } else {
                // ❌ Show toast, disable button
                showToast(requireContext(), getString(R.string.feature_not_available_for_now))
            }
        } else {
            showToast(requireContext(), getString(R.string.no_internet_connection))
        }
    }

    private fun showFilePicker() {
        filePickerHelper.pickExcelFile(
            onFilePicked = { inputStream ->
                lifecycleScope.launch {
                    viewModel.importDataFromExcel(
                        inputStream,
                        onComplete = { message, importResult ->
                            if (importResult != null) {
                                ImportResultDialog(requireContext(), importResult).show()
                            } else {
                                showToast(requireContext(), message)
                            }
                        },
                        onError = { errorMessage ->
                            showToast(requireContext(), errorMessage)
                        }
                    )
                }
            },
            onError = { exception ->
                showToast(requireContext(), exception.message.toString())
            }

        )
    }


    private fun setupRecyclerView() {
        adapter = AttendanceSummaryAdapter { employeeId ->
            val bundle = Bundle().apply {
                putLong("employeeId", employeeId)
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
                viewModel.setSelectedMonth(month?.plus(1))
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
            adapter.loadStateFlow.collect { loadState ->
                // Show/hide progress bar based on loading state
                binding.pbHistory.visibility = when (loadState.refresh) {
                    is LoadState.Loading -> View.VISIBLE
                    else -> View.GONE
                }

                // Show/hide empty state based on whether the list is empty
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.txtEmptyHistory.visibility = if (isListEmpty) View.VISIBLE else View.GONE
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