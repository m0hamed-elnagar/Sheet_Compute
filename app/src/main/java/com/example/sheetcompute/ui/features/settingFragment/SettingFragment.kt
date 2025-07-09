package com.example.sheetcompute.ui.features.settingFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sheetcompute.databinding.FragmentSettingBinding
import com.example.sheetcompute.ui.subFeatures.utils.saveXlsTemplateToDownloads
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalTime
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewmodel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner for days 1-28
        val days = (1..28).toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDayOfMonth.adapter = adapter

        // Observe month start day
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthStartDay.collectLatest { day ->
                    val pos = days.indexOf(day)
                    if (pos >= 0 && binding.spinnerDayOfMonth.selectedItemPosition != pos) {
                        binding.spinnerDayOfMonth.setSelection(pos)
                    }
                }
            }
        }

        // Observe work start time
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.workStartTime.collectLatest { time ->
                    binding.textStartTime.text = time.toString()
                }
            }
        }

        // Update month start day on selection
        binding.spinnerDayOfMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDay = (parent.getItemAtPosition(position) as Int)
                if (viewModel.monthStartDay.value != selectedDay) {
                    viewModel.setMonthStartDay(selectedDay)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.buttonPickTime.setOnClickListener {
            showTimePickerDialog()
        }
        binding.buttonSave.setOnClickListener {
            viewModel.saveSettings()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.buttonDownloadTemplate.setOnClickListener {
            saveXlsTemplateToDownloads(requireContext())
        }
    }
    //todo get this dialog it's own file
    private fun showTimePickerDialog() {
        val currentTime = viewModel.workStartTime.value
        val timePicker = TimePicker(requireContext()).apply {
            hour = currentTime.hour
            minute = currentTime.minute
            setIs24HourView(false)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Work Start Time")
            .setView(timePicker)
            .setPositiveButton("Set Time") { _, _ ->
                val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)
                viewModel.setWorkStartTime(selectedTime)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}