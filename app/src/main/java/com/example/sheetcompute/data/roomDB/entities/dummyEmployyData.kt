package com.example.sheetcompute.data.roomDB.entities


import com.example.sheetcompute.data.roomDB.entities.AttendanceItem
import java.util.*
import kotlin.random.Random

object RealAttendanceData {
    // Real employee data (replace with your actual employee IDs and names)
    private val realEmployees = listOf(
        EmployeeData("EMP1001", "Ahmed Mahmoud"),
        EmployeeData("EMP1002", "Mohamed Ali"),
        EmployeeData("EMP1003", "Fatima Hassan"),
        EmployeeData("EMP1004", "Aisha Omar"),
        EmployeeData("EMP1005", "Youssef Khalid")
    )

    // Real company working hours (9:00 AM to 5:00 PM)
    private val workStartHour = 9
    private val workEndHour = 17
    private val lunchStartHour = 13
    private val lunchEndHour = 14

    // Generate realistic attendance records for the current month
    fun generateCurrentMonthAttendance(): List<AttendanceItem> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return realEmployees.flatMap { employee ->
            (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).mapNotNull { day ->
                calendar.set(currentYear, currentMonth, day)

                // Skip weekends (adjust for your company policy)
                if (calendar.get(Calendar.DAY_OF_WEEK) in setOf(Calendar.FRIDAY, Calendar.SATURDAY)) {
                    return@mapNotNull null
                }

                // 90% attendance rate
                if (Random.nextFloat() > 0.9f) {
                    return@mapNotNull null // Skip for absent days
                }

                // Generate realistic check-in times
                val (loginTime, status, lateDuration) = generateCheckInData(calendar)

                AttendanceItem(
                    Id = day * 1000 + employee.id.hashCode(), // Unique ID
                    employeeId = employee.id,
                    logInTime = loginTime,
                    lateDuration = lateDuration,
                    status = status
                )
            }
        }.sortedByDescending { it.logInTime }
    }

    private fun generateCheckInData(dayCalendar: Calendar): Triple<Date, String, String?> {
        val calendar = dayCalendar.clone() as Calendar

        // 70% on time, 20% late, 10% half day
        when (Random.nextInt(0, 10)) {
            in 0..6 -> { // On time
                calendar.set(Calendar.HOUR_OF_DAY, workStartHour)
                calendar.set(Calendar.MINUTE, Random.nextInt(0, 30)) // 9:00-9:30 AM
                return Triple(calendar.time, "Present", null)
            }
            in 7..8 -> { // Late
                calendar.set(Calendar.HOUR_OF_DAY, workStartHour)
                calendar.set(Calendar.MINUTE, Random.nextInt(31, 120)) // 9:31-11:00 AM
                val lateMinutes = calendar.get(Calendar.MINUTE) + (calendar.get(Calendar.HOUR_OF_DAY) - workStartHour) * 60
                return Triple(calendar.time, "Late", "$lateMinutes min")
            }
            else -> { // Half day
                calendar.set(Calendar.HOUR_OF_DAY, lunchEndHour + Random.nextInt(0, 2)) // 2:00-3:00 PM
                calendar.set(Calendar.MINUTE, Random.nextInt(0, 60))
                return Triple(calendar.time, "HalfDay", null)
            }
        }
    }

    private data class EmployeeData(val id: String, val name: String)
}