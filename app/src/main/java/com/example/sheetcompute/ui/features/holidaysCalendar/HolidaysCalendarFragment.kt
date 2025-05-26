package com.example.sheetcompute.ui.features.holidaysCalendar

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sheetcompute.R
import com.example.sheetcompute.databinding.FragmentHolidaysCalendarBinding
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils
import com.example.sheetcompute.ui.subFeatures.utils.WeekendSelectionDialogFragment
import com.example.sheetcompute.ui.subFeatures.utils.showSingleDayPickerDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HolidaysCalendarFragment : Fragment() {
    private lateinit var _binding: FragmentHolidaysCalendarBinding
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private var weekendDays = listOf(DayOfWeek.FRIDAY) // Default to Friday only

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolidaysCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupHolidayButton()

        binding.btnEditWeekend.setOnClickListener {
            WeekendSelectionDialogFragment.show(childFragmentManager,viewModel.weekendDays.value) { selectedDays ->
                // Send the selected days to the ViewModel
                viewModel.updateWeekendDays(selectedDays)
            }
        }
    }
private fun setupObservers() {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
          viewModel.weekendDays.collectLatest { weekendDays ->
            binding.tvWeekendSelection.text = if (weekendDays.isNullOrEmpty()) {
                "No weekend selected"
            } else {
                weekendDays.joinToString(", ") { it.name }
            }
        }
        }



} }
    private fun setupHolidayButton() {
        binding.btnAddHoliday.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menuInflater.inflate(R.menu.holiday_type_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_single_day -> {
                        showSingleDayPickerDialog(requireActivity().supportFragmentManager) { selectedDate ->
                            //todo save single day in viewmodel  as start and end with the same date
                        }
                        true
                    }

                    R.id.menu_range -> {
                        DatePickerUtils.showRangePickerDialog(
                            requireActivity().supportFragmentManager
                        ) { startDate, endDate ->

                            //todo save range in viewmodel
                        }
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }
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
