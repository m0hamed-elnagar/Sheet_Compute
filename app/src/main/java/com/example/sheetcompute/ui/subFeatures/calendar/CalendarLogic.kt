package com.example.sheetcompute.ui.subFeatures.calendar

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.example.sheetcompute.R
import com.example.sheetcompute.ui.features.holidaysCalendar.CalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import kotlin.system.measureTimeMillis

class CalendarLogic(
    private val context: Context,
    private val viewModel: CalendarViewModel
) {

    internal val calendarDecorator = CalendarDecorator(context, viewModel)

    fun setupCalendar(calendarView: CalendarView) {
        calendarView.setHeaderColor(R.color.colorPrimary)
        calendarView.setHeaderLabelColor(R.color.white)
   ContextCompat.getDrawable(context, R.drawable.ic_chevron_left)?.let {
            calendarView.setPreviousButtonImage(it)
        }
        ContextCompat.getDrawable(context, R.drawable.ic_chevron_right)?.let {
            calendarView.setForwardButtonImage(it)
        }
    }

    fun setupPageChangeListeners(calendarView: CalendarView) {

        val listener = object : OnCalendarPageChangeListener {
            override fun onChange() {
                  }
        }

        calendarView.setOnForwardPageChangeListener(listener)
        calendarView.setOnPreviousPageChangeListener(listener)

        calendarView.setOnPagePrepareListener { cal->
            val currentMonth = calendarView.currentPageDate.toYearMonth()
            viewModel.loadHolidaysForMonth(currentMonth)
            updateCalendarEvents(cal)
        }

        }

    fun updateCalendarEvents(calendarView: Calendar): ArrayList<CalendarDay> {
             return   calendarDecorator.decorateDays(calendarView)


    }
    private fun java.util.Calendar.toYearMonth(): YearMonth {
        return this.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .let { YearMonth.from(it) }
    }
}