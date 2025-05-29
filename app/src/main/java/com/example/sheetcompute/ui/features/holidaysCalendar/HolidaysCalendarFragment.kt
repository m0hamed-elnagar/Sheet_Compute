package com.example.sheetcompute.ui.features.holidaysCalendar

import android.R.attr.label
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.entities.Holiday
import com.example.sheetcompute.databinding.FragmentHolidaysCalendarBinding
import com.example.sheetcompute.ui.subFeatures.calendar.*
import com.example.sheetcompute.ui.subFeatures.calendar.CalendarLogic
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils.showSingleDayPickerDialog
import com.example.sheetcompute.ui.subFeatures.utils.WeekendSelectionDialogFragment
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*
import kotlin.text.get

class HolidaysCalendarFragment : Fragment() {
    private var _binding: FragmentHolidaysCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var holidayAdapter: HolidayAdapter
    private lateinit var calendarView: com.applandeo.materialcalendarview.CalendarView
    private lateinit var calendarLogic: CalendarLogic

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolidaysCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarLogic = CalendarLogic(requireContext(), viewModel)

        calendarView = binding.calendarView
        calendarLogic.setupCalendar(calendarView)
        calendarLogic.setupPageChangeListeners(calendarView)

        setupRecyclerView()
        setupObservers()
        setupButtons()
    }

    private fun setupRecyclerView() {
        holidayAdapter = HolidayAdapter(
            onDeleteClick = { holiday -> showDeleteHolidayDialog(holiday) },
            onEditClick = { holiday -> showEditHolidayDialog(holiday) }
        )

        binding.holidaysRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = holidayAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weekendDays.collect { weekendDays ->
                        binding.tvWeekendSelection.text = if (weekendDays.isEmpty()) {
                            getString(R.string.no_weekend_selected)
                        } else {
                            weekendDays.joinToString(", ") { it.name }
                        }
                        updateCalendarEvents(calendarView)
                    }
                }

                launch {
                    viewModel.holidays.collect { holidays ->
                        holidayAdapter.submitList(holidays)
                        updateCalendarEvents(calendarView)
                    }
                }
            }
        }
    }

    private fun showDayEventsDialog(date: Date) {
        val calendar = Calendar.getInstance().apply { time = date }
        val localDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val holidays = viewModel.holidays.value.filter {
            !localDate.isBefore(it.startDate) && !localDate.isAfter(it.endDate)
        }

        if (holidays.isNotEmpty()) {
            val holidayNames = holidays.joinToString("\n") { it.name }
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.holidays_on_date, localDate.toString()))
                .setMessage(holidayNames)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }

    private fun setupButtons() {
        binding.btnEditWeekend.setOnClickListener {
            WeekendSelectionDialogFragment.show(
                childFragmentManager,
                viewModel.weekendDays.value
            ) { selectedDays ->
                viewModel.updateWeekendDays(selectedDays)
            }
        }

        binding.btnAddHoliday.setOnClickListener { view ->
            showHolidayTypeMenu(view)
        }
    }

    private fun showHolidayTypeMenu(anchorView: View) {
        PopupMenu(requireContext(), anchorView).apply {
            menuInflater.inflate(R.menu.holiday_type_menu, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_single_day -> {
                        showSingleDayPickerDialog(childFragmentManager) { date ->
                            showHolidayDetailsDialog(date, date)
                        }
                        true
                    }

                    R.id.menu_range -> {
                        DatePickerUtils.showRangePickerDialog(childFragmentManager) { startDate, endDate ->
                            showHolidayDetailsDialog(startDate, endDate)
                        }
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }

    private fun showHolidayDetailsDialog(startDate: LocalDate, endDate: LocalDate) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_holiday_details, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etHolidayName)
        val noteEditText = dialogView.findViewById<EditText>(R.id.etHolidayNote)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_holiday))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                viewModel.addHoliday(
                    Holiday(
                        startDate = startDate,
                        endDate = endDate,
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString()
                    )
                )
                updateCalendarEvents(calendarView)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    fun updateCalendarEvents(calendarView: CalendarView) {
        val decoratedDays = calendarLogic.calendarDecorator.decorateDays(calendarView.currentPageDate)
        calendarView.setCalendarDays(decoratedDays)
    }

    private fun showEditHolidayDialog(holiday: Holiday) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_holiday_details, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etHolidayName)
        val noteEditText = dialogView.findViewById<EditText>(R.id.etHolidayNote)

        nameEditText.setText(holiday.name)
        noteEditText.setText(holiday.note)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_holiday))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                viewModel.updateHoliday(
                    holiday.copy(
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString()
                    )
                )
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteHolidayDialog(holiday: Holiday) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_holiday_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteHoliday(holiday)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun LocalDate.toCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthValue - 1)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

