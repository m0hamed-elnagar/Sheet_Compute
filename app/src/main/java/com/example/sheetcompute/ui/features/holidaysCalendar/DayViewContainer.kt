package com.example.sheetcompute.ui.features.holidaysCalendar

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.entities.Holiday
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek

class DayViewContainer(view: View) : ViewContainer(view) {
    private val textView: TextView = view.findViewById(R.id.calendarDayText)

    fun bind(
        day: CalendarDay,
        weekendDays: Set<DayOfWeek>,
        holidays: List<Holiday>
    ) {
        textView.text = day.date.dayOfMonth.toString()

        // Reset appearance
        textView.setBackgroundColor(Color.TRANSPARENT)
        textView.setTextColor(Color.BLACK)

        // Weekend styling
        if (weekendDays.contains(day.date.dayOfWeek)) {
            textView.setTextColor(Color.BLUE)
            textView.setBackgroundColor(Color.LTGRAY)
        }

        // Holiday styling
        holidays.firstOrNull { holiday ->
            !day.date.isBefore(holiday.startDate) &&
                    !day.date.isAfter(holiday.endDate)
        }?.let {
            textView.setTextColor(Color.WHITE)
            textView.setBackgroundColor(Color.RED)
        }
    }
}