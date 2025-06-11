package com.example.sheetcompute.ui.subFeatures.spinners

import android.R
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
    private val onYearSelected: (Int?) -> Unit,
    private val onMonthSelected: (Int?) -> Unit
) {
    private var yearSelectionJob: Job? = null
    private var monthSelectionJob: Job? = null

    init {
        setupDateFilters()
    }

    private fun setupDateFilters() {
        // Setup year spinner
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 10..currentYear).reversed().map { it.toString() }
        ArrayAdapter(
            yearSpinner.context,
            R.layout.simple_spinner_item,
            years
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            yearSpinner.adapter = adapter
        }

        // Setup month spinner
        val months = listOf("All Months") + (0..11).map {
            Calendar.getInstance().apply { set(Calendar.MONTH, it) }.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.getDefault()
            ) ?: ""
        }
        ArrayAdapter(
            monthSpinner.context,
            R.layout.simple_spinner_item,
            months
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            monthSpinner.adapter = adapter
        }
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        monthSpinner.setSelection(currentMonth + 1)

        // Year selection listener
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                yearSelectionJob?.cancel()
                yearSelectionJob = coroutineScope.launch {
                    delay(300) // Debounce
                    val year = parent.getItemAtPosition(position).toString().toIntOrNull()
                    Log.d("DateFilterHandler", "Year selected: $year")
                    onYearSelected(year)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                monthSelectionJob?.cancel()
                monthSelectionJob = coroutineScope.launch {
                    delay(300) // Debounce
//                    val month = if (position == 0) null else position - 1
                    Log.d("DateFilterHandler", "Month selected: $position")
                    onMonthSelected(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun setSelections(year: Int?, month: Int?) {
        year?.let {
            val position = (yearSpinner.adapter as? ArrayAdapter<*>)?.let { adapter ->
                (0 until adapter.count).indexOfFirst { pos ->
                    adapter.getItem(pos) == it.toString()
                }
            }
            if (position != null && position >= 0) {
                yearSpinner.setSelection(position)
            }
        }

        month?.let {
            if (it + 1 < monthSpinner.adapter.count) {
                monthSpinner.setSelection(it + 1)
            }
        }
    }

    fun cleanup() {
        yearSelectionJob?.cancel()
        monthSelectionJob?.cancel()
        yearSpinner.onItemSelectedListener = null
        monthSpinner.onItemSelectedListener = null
    }
}