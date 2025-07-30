package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory


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
import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.databinding.FragmentSearchEmployeeBinding
import com.example.sheetcompute.ui.subFeatures.dialogs.ImportResultDialog
import com.example.sheetcompute.ui.subFeatures.helpers.ExcelImportHelper
import com.example.sheetcompute.ui.subFeatures.sheetPicker.FilePickerFragmentHelper
import com.example.sheetcompute.ui.subFeatures.utils.isInternetAvailable
import com.example.sheetcompute.ui.subFeatures.utils.scrollToTop
import com.example.sheetcompute.ui.subFeatures.utils.showToast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchEmployeeFragment  : Fragment() {
    private var _binding: FragmentSearchEmployeeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchEmployeeAdapter
    private var searchJob: Job? = null
    private lateinit var filePickerHelper: FilePickerFragmentHelper
    private lateinit var excelImportHelper: ExcelImportHelper
    @Inject
    lateinit var preferencesGateway: PreferencesGateway

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchEmployeeBinding.inflate(inflater, container, false)
        filePickerHelper = FilePickerFragmentHelper(this)
        excelImportHelper = ExcelImportHelper(
            requireContext(),
            preferencesGateway,
            filePickerHelper,
            viewLifecycleOwner.lifecycleScope // Pass the scope here
        ) { inputStream, onComplete, onError ->
            viewModel.importDataFromExcel(inputStream, onComplete, onError)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeData()
        binding.importSheet.setOnClickListener { excelImportHelper.showImportDialog() }
    }


    private fun setupRecyclerView() {
        adapter = SearchEmployeeAdapter { employeeId ->
            val bundle = bundleOf("employeeId" to employeeId)
            findNavController().navigate(R.id.employeeAttendanceFragment, bundle)
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@SearchEmployeeFragment.adapter
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

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.filteredEmployees.collectLatest { pagingData ->
                adapter.submitList(pagingData)
                _binding?.rvHistory?.apply {
                    this.scrollToTop()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isEmpty.collect { isEmpty ->
                _binding?.txtEmptyHistory?.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                _binding?.pbHistory?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
    }
}