package com.example.sheetcompute.ui.features.holidaysCalendar.calendar

import android.view.View
import android.widget.TextView
import com.example.sheetcompute.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.DayOfWeek

class CalendarSetup(
  private val  calendarView: CalendarView,
  private val  currentMonth: YearMonth = YearMonth.now(),
  private val  firstDayOfWeek: DayOfWeek,
) {
    private var holidays: Set<LocalDate> = emptySet()
    private var weekendDays: Set<DayOfWeek> = emptySet()
    private var onDayClick: ((LocalDate) -> Unit)? = null
    fun setHolidays(holidays: Set<LocalDate>) {
        this.holidays = holidays
    }

    fun setWeekends(weekends: Set<DayOfWeek>) {
        this.weekendDays = weekends
    }

    fun setOnDayClick(listener: (LocalDate) -> Unit) {
        this.onDayClick = listener
    }
    fun setupCalendar(

    ) {

        val firstMonth = currentMonth.minusMonths(100)
        val lastMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        // Set up the day binder first
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)


override fun bind(container: DayViewContainer, data: CalendarDay) {
    if (data.position == DayPosition.MonthDate) {
        // Show the valid day
        container.dayTextView.visibility = View.VISIBLE
        container.dayTextView.text = data.date.dayOfMonth.toString()

        val backgroundRes = when {
            holidays.contains(data.date) -> R.drawable.holiday_bg
            weekendDays.contains(data.date.dayOfWeek) -> R.drawable.weekend_bg
            else -> R.drawable.working_day_bg
        }
        container.dayTextView.setBackgroundResource(backgroundRes)

        // Optional: handle click
        container.dayTextView.setOnClickListener {
            onDayClick?.invoke(data.date)
        }

    } else {
        // Hide out-of-month days
        container.dayTextView.visibility = View.GONE
        container.dayTextView.text = ""
        container.dayTextView.setBackgroundResource(0)
        container.dayTextView.setOnClickListener(null)
    }
}
        }
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = buildString {
                    append(month.yearMonth.month.name.lowercase().replaceFirstChar { it.titlecase() })
                    append(" ")
                    append(month.yearMonth.year)
                }
            }
        }
    }

    class DayViewContainer(view: View) : ViewContainer(view) {
        val dayTextView: TextView = view.findViewById(R.id.calendarDayText)
    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.monthTextView)
    }
}