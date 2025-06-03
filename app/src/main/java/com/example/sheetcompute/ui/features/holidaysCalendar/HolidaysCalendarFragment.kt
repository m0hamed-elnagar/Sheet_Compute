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
import com.example.sheetcompute.ui.subFeatures.utils.WeekendSelectionDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

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
        setupCalendar()
        setupRecyclerView()
        setupObservers()
        setupButtons()
        viewModel.loadInitialData()
    }

    private fun setupCalendar() {
        calendarSetup = CalendarSetup(
            binding.calendarView, viewModel.currentMonth.value,
            DayOfWeek.SUNDAY
        )
        calendarSetup.setupCalendar()
        binding.calendarView.monthScrollListener = { month ->
            val newMonth = month.yearMonth
            if (newMonth != viewModel.currentMonth.value) {
                viewModel.updateCurrentMonth(newMonth)
                Log.d("HolidaysCalendarFragment", "Current month updated: $newMonth")
            }
        }
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
                launch {
                    viewModel.weekendDays.collect { weekendDays ->
                        Log.d("HolidaysCalendarFragment", "Weekend days updated: $weekendDays")
                        calendarSetup.setWeekends(weekendDays)
                        binding.calendarView.notifyCalendarChanged()
                        updateWeekendSelectionLabel(weekendDays)
                    }
                }
                viewModel.loading.observe(viewLifecycleOwner) {
                    binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
                }
                launch {
                    viewModel.holidaysForCurrentMonth
                        .collect { holidays ->
                            binding.calendarView.notifyCalendarChanged()
                            holidayAdapter.submitList(holidays)
                        }
                }
                subscribeToHolidaysEvents()
            }
        }
    }

    private fun CoroutineScope.subscribeToHolidaysEvents() {
        launch {
            viewModel.holidaysEvents.collect { holidays ->
                onHolidaysChanged(holidays)
            }
        }
    }

    private fun onHolidaysChanged(holidays: Set<LocalDate>) {
        calendarSetup.setHolidays(holidays)
        binding.calendarView.notifyCalendarChanged()
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
                        DatePickerUtils.showSingleDayPickerDialog(childFragmentManager) { date ->
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

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_holiday))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                if (name.isEmpty()) {
                    nameEditText.error = getString(R.string.holiday_name_required)
                } else {
                    viewModel.addHoliday(
                        Holiday(
                            startDate = startDate,
                            endDate = endDate,
                            name = name,
                            note = noteEditText.text.toString().trim()
                        )
                    )
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun showEditHolidayDialog(holiday: Holiday) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_holiday_details, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.etHolidayName)
        val noteEditText = dialogView.findViewById<EditText>(R.id.etHolidayNote)

        nameEditText.setText(holiday.name)
        noteEditText.setText(holiday.note)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_holiday))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                if (name.isEmpty()) {
                    nameEditText.error = getString(R.string.holiday_name_required)
                } else {
                    viewModel.updateHoliday(
                        holiday.copy(
                            name = name,
                            note = noteEditText.text.toString().trim()
                        )
                    )
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
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

    private fun updateWeekendSelectionLabel(weekendDays: Set<DayOfWeek>) {
        val label = if (weekendDays.isEmpty()) {
            getString(R.string.no_weekend_selected)
        } else {
            weekendDays.joinToString(", ") { it.name }
        }
        binding.tvWeekendSelection.text = label
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

