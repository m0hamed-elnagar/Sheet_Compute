package com.example.sheetcompute.ui.features.holidaysCalendar

import android.graphics.Color
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
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.entities.Holiday
import com.example.sheetcompute.databinding.FragmentHolidaysCalendarBinding
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils.showSingleDayPickerDialog
import com.example.sheetcompute.ui.subFeatures.utils.WeekendSelectionDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class HolidaysCalendarFragment : Fragment() {
    private var _binding: FragmentHolidaysCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var holidayAdapter: HolidayAdapter
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolidaysCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupButtons()
//        setupCalendar()
    }

  private fun setupRecyclerView() {
    holidayAdapter = HolidayAdapter(
        onDeleteClick = { holiday ->
            showDeleteHolidayDialog(holiday)
        },
        onEditClick = { holiday ->
            showEditHolidayDialog(holiday)
        }
    )

    binding.holidaysRecyclerView.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = holidayAdapter
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
}

    private fun showEditHolidayDialog(holiday: Holiday) {
//        HolidayDetailsDialogFragment.newInstance(holiday)
//            .show(childFragmentManager, "EditHolidayDialog")
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
                    }
                }

                launch {
                    viewModel.holidays.collect { holidays ->
                        holidayAdapter.submitList(holidays)
                    }
                }
            }
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
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.holiday_type_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_single_day -> {
                    showSingleDayPickerDialog(requireActivity().supportFragmentManager) { date ->
                        showHolidayDetailsDialog(date, date)
                    }
                    true
                }
                R.id.menu_range -> {
                    DatePickerUtils.showRangePickerDialog(
                        requireActivity().supportFragmentManager
                    ) { startDate, endDate ->
                        showHolidayDetailsDialog(startDate, endDate)
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

        private fun showHolidayDetailsDialog(startDate: LocalDate, endDate: LocalDate) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_holiday_details, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etHolidayName)
        val noteEditText = dialogView.findViewById<EditText>(R.id.etHolidayNote)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Holiday")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val name = nameEditText.text.toString()
                val note = noteEditText.text.toString()
                val holiday = Holiday(
                    startDate = startDate,
                    endDate = endDate,
                    name = name,
                    note = note
                )
                viewModel.addHoliday(holiday)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun showDeleteHolidayDialog(holiday: Holiday) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this holiday?")
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteHoliday(holiday)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

//    private fun setupCalendar() {
//        val currentMonth = YearMonth.now()
//        binding.calendarView.setup(
//            startMonth = currentMonth.minusMonths(12),
//            endMonth = currentMonth.plusMonths(12),
//            firstDayOfWeek = DayOfWeek.SUNDAY
//        )
//
//        // Set up day binder
//        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.bind(
//                    day = data,
//                    weekendDays = viewModel.weekendDays.value,
//                    holidays = viewModel.holidays.value
//                )
//            }
//        }
//
//        // Set month scroll listener
//        binding.calendarView.monthScrollListener = { month ->
//            viewModel.loadHolidaysForMonth(month.yearMonth)
//        }
//
//        // Load initial data
//        viewModel.loadHolidaysForMonth(currentMonth)
//    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)

        fun bind(day: CalendarDay, weekendDays: Set<DayOfWeek>, holidays: List<Holiday>) {
            textView.text = day.date.dayOfMonth.toString()

            var textColor = ContextCompat.getColor(requireContext(), R.color.working_day_text)
            var bgColor = ContextCompat.getColor(requireContext(), R.color.working_day_bg)

            if (weekendDays.contains(day.date.dayOfWeek)) {
                textColor = ContextCompat.getColor(requireContext(), R.color.weekend_text)
                bgColor = ContextCompat.getColor(requireContext(), R.color.weekend_bg)
            } else if (holidays.any { !day.date.isBefore(it.startDate) && !day.date.isAfter(it.endDate) }) {
                textColor = ContextCompat.getColor(requireContext(), R.color.holiday_text)
                bgColor = ContextCompat.getColor(requireContext(), R.color.holiday_bg)
            }

            // Apply styles
            textView.setTextColor(textColor)
            textView.setBackgroundColor(bgColor)
        }
    }
    fun YearMonth.atStartOfMonth(): LocalDate = this.atDay(1)
    fun YearMonth.atEndOfMonth(): LocalDate = this.atEndOfMonth()
    fun daysOfWeek(): List<DayOfWeek> = DayOfWeek.values().toList()
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//    private fun setupCalendar() {
//        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, day: CalendarDay) {
//                container.textView.text = day.date.dayOfMonth.toString()
//
//                // Reset appearance
//                container.textView.setBackgroundColor(Color.TRANSPARENT)
//                container.textView.setTextColor(Color.BLACK)
//
//                // Check if it's a weekend day
//                if (weekendDays.contains(day.date.dayOfWeek)) {
//                    container.textView.setTextColor(Color.BLUE)
//                    container.textView.setBackgroundColor(Color.LTGRAY)
//                }
//
//                // Check if it's a holiday
//                holidays.firstOrNull { holiday ->
//                    when (holiday) {
//                        is SingleDayHoliday -> holiday.date.isEqual(day.date)
//                        is RangeHoliday -> !day.date.isBefore(holiday.startDate) &&
//                                           !day.date.isAfter(holiday.endDate)
//                    }
//                }?.let {
//                    container.textView.setTextColor(Color.WHITE)
//                    container.textView.setBackgroundColor(Color.RED)
//                }
//            }
//        }
//    }

//    private fun setupButtons() {
//        binding.btnAddSingleDay.setOnClickListener {
//            showDatePicker { date ->
//                showHolidayDetailsDialog(date, date)
//            }
//        }
//
//        binding.btnAddRange.setOnClickListener {
//            showRangePicker()
//        }
//    }


//    private fun setupHolidayList() {
//        binding.holidaysRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.holidaysRecyclerView.adapter = HolidayAdapter(holidays) { holiday ->
//            holidays.remove(holiday)
//            updateHolidayList()
//            binding.calendarView.notifyCalendarChanged()
//        }
//    }

//    private fun updateHolidayList() {
//        (binding.holidaysRecyclerView.adapter as HolidayAdapter).submitList(holidays.toList())
//    }
//}
//
//class DayViewContainer(view: View) : ViewContainer(view) {
//    val textView: TextView = view.findViewById(R.id.calendarDayText)
//}
//
