package com.example.sheetcompute.ui.subFeatures.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.utils.calendar
import com.example.sheetcompute.R
import com.example.sheetcompute.ui.features.holidaysCalendar.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import kotlin.text.get
import kotlin.text.set

class CalendarDecorator(
    private val context: Context,
    private val viewModel: CalendarViewModel
) {

    private val weekendDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.weekend_bg)!!
    }
    private val holidayDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.holiday_bg)!!
    }
    private val workingDayDrawable: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.working_day_bg)!!
    }


  public  fun decorateDays(calendar:Calendar): ArrayList<CalendarDay> {
        val holidays = viewModel.holidays.value
        val weekendDays = viewModel.weekendDays.value.map { it.value }.toSet()

        val currentMonth = calendar
        val startCalendar = (currentMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -1)
        }
        val endCalendar = (currentMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 2)
        }
        val calendarDays = ArrayList<com.applandeo.materialcalendarview.CalendarDay>(32)
        val calendar = startCalendar.clone() as Calendar

        while (calendar.before(endCalendar)) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val isHoliday = holidays.any { holiday ->
                !holiday.startDate.isAfter(LocalDate.of(year, month, day)) &&
                !holiday.endDate.isBefore(LocalDate.of(year, month, day))
            }

            val drawable = when {
                isHoliday ->  holidayDrawable
                weekendDays.contains(dayOfWeek) -> weekendDrawable
                else -> workingDayDrawable
            }

            val calendarDay = com.applandeo.materialcalendarview.CalendarDay(
                calendar.clone() as Calendar
            )
            calendarDay.backgroundDrawable =drawable
            calendarDays.add(calendarDay)
            calendar.add(Calendar.DATE, 1)
        }
       return calendarDays;
    }
}

