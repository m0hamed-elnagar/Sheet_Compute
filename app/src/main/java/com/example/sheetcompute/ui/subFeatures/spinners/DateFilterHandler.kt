package com.example.sheetcompute.ui.subFeatures.spinners

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class DateFilterHandler(
    private val yearSpinner: Spinner,
    private val monthSpinner: Spinner,
    private val coroutineScope: LifecycleCoroutineScope,
    private val includeAllMonths: Boolean = true,
    private val onYearSelected: (Int?) -> Unit,
    private val onMonthSelected: (Int?) -> Unit
) {
    private var yearSelectionJob: Job? = null
    private var monthSelectionJob: Job? = null
    private var isUpdating = false
    private var monthNamesToValues = mapOf<String, Int?>()

    init {
        setupListeners()
    }

    fun updateYears(years: List<Int>) {
        isUpdating = true
        val yearStrings = years.map { it.toString() }
        val currentSelection = yearSpinner.selectedItem?.toString()
        
        ArrayAdapter(
            yearSpinner.context,
            android.R.layout.simple_spinner_item,
            yearStrings
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            yearSpinner.adapter = adapter
        }

        // Restore selection if possible
        currentSelection?.let { sel ->
            val pos = yearStrings.indexOf(sel)
            if (pos >= 0) yearSpinner.setSelection(pos)
        }
        isUpdating = false
    }

    fun updateMonths(months: List<Int>) {
        isUpdating = true
        val mapping = mutableMapOf<String, Int?>()
        if (includeAllMonths && months.isNotEmpty()) {
            mapping["All Months"] = null
        }
        
        months.forEach { month0Based ->
            val name = Calendar.getInstance().apply { set(Calendar.MONTH, month0Based) }.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.getDefault()
            ) ?: (month0Based + 1).toString()
            mapping[name] = month0Based
        }
        
        monthNamesToValues = mapping
        val finalMonths = mapping.keys.toList()

        val currentSelection = monthSpinner.selectedItem?.toString()

        ArrayAdapter(
            monthSpinner.context,
            android.R.layout.simple_spinner_item,
            finalMonths
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthSpinner.adapter = adapter
        }

        // Restore selection if possible
        currentSelection?.let { sel ->
            val pos = finalMonths.indexOf(sel)
            if (pos >= 0) monthSpinner.setSelection(pos)
        }
        isUpdating = false
    }

    private fun setupListeners() {
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUpdating) return
                yearSelectionJob?.cancel()
                yearSelectionJob = coroutineScope.launch {
                    delay(300)
                    val year = parent.getItemAtPosition(position).toString().toIntOrNull()
                    onYearSelected(year)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUpdating) return
                monthSelectionJob?.cancel()
                monthSelectionJob = coroutineScope.launch {
                    delay(300)
                    val selectedName = parent.getItemAtPosition(position).toString()
                    onMonthSelected(monthNamesToValues[selectedName])
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun setSelections(year: Int?, month: Int?) {
        isUpdating = true
        year?.let { y ->
            val adapter = yearSpinner.adapter as? ArrayAdapter<String>
            if (adapter != null) {
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i) == y.toString()) {
                        yearSpinner.setSelection(i)
                        break
                    }
                }
            }
        }

        month?.let { m ->
            val monthName = Calendar.getInstance().apply { set(Calendar.MONTH, m) }.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.getDefault()
            )
            val adapter = monthSpinner.adapter as? ArrayAdapter<String>
            if (adapter != null) {
                for (i in 0 until adapter.count) {
                    if (adapter.getItem(i) == monthName) {
                        monthSpinner.setSelection(i)
                        break
                    }
                }
            }
        }
        isUpdating = false
    }

    fun cleanup() {
        yearSelectionJob?.cancel()
        monthSelectionJob?.cancel()
        yearSpinner.onItemSelectedListener = null
        monthSpinner.onItemSelectedListener = null
    }
}