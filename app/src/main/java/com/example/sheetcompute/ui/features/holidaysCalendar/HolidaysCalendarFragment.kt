package com.example.sheetcompute.ui.features.holidaysCalendar


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
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
import com.example.sheetcompute.ui.features.holidaysCalendar.calendar.CalendarSetup
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils
import com.example.sheetcompute.ui.subFeatures.utils.DatePickerUtils.showSingleDayPickerDialog
import com.example.sheetcompute.ui.subFeatures.utils.WeekendSelectionDialogFragment
import com.kizitonwose.calendar.view.CalendarView
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

class HolidaysCalendarFragment : Fragment() {
    private var _binding: FragmentHolidaysCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var holidayAdapter: HolidayAdapter
    private lateinit var calendarSetup: CalendarSetup
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
        val calendarView: CalendarView = binding.calendarView

        calendarSetup = CalendarSetup(
            binding.calendarView, viewModel.currentMonth.value,
            DayOfWeek.SUNDAY
        )
        calendarSetup.setupCalendar()
        calendarView.monthScrollListener = { month ->
            val newMonth = month.yearMonth
            if (newMonth != viewModel.currentMonth.value) {
                viewModel.updateCurrentMonth(newMonth)
                Log.d("HolidaysCalendarFragment", "Current month updated: $newMonth")
            }
        }
        if (calendarView.adapter != null) {
            calendarView.notifyCalendarChanged()
        } else {
            Log.e("HolidaysCalendarFragment", "CalendarView adapter is not initialized yet.")
        }
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        setupRecyclerView()

        setupObservers()
        setupButtons()
        viewModel.loadInitialData()
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
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Launch weekendDays collector separately
                launch {
                    viewModel.weekendDays.collect { weekendDays ->
                        Log.d("HolidaysCalendarFragment", "Weekend days updated: $weekendDays")
                        calendarSetup.setWeekends(weekendDays)
                        binding.calendarView.notifyCalendarChanged()

                        val label = if (weekendDays.isEmpty()) {
                            getString(R.string.no_weekend_selected)
                        } else {
                            weekendDays.joinToString(", ") { it.name }
                        }
                        binding.tvWeekendSelection.text = label
                    }
                }



                launch {
                    viewModel.holidaysForCurrentMonth.collect {holidays->
                            binding.calendarView.notifyCalendarChanged()
                        holidayAdapter.submitList(holidays)
                    }
                }

                launch {
                    viewModel.holidaysEvents.collect { holidays ->

                        calendarSetup.setHolidays(holidays)
                        binding.calendarView.notifyCalendarChanged()
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
                Log.d("Calendar", "setupButtons: Selected weekend days: $selectedDays")
                viewModel.updateWeekendDays(selectedDays)
                binding.calendarView.notifyCalendarChanged()
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
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

