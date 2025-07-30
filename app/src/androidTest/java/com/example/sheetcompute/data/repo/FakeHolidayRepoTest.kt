//package com.example.sheetcompute.data.repo
//
//import androidx.paging.Pager
//import com.example.sheetcompute.data.entities.AttendanceRecord
//import com.example.sheetcompute.data.entities.AttendanceRecordUI
//import org.junit.Before
//import java.time.LocalDate
//
//
//class FakeHolidayRepoTest: AttendanceRepoInterface {
//    private lateinit var attendanceRecords: MutableList<AttendanceRecord>
//
//    override suspend fun getEmployeeAttendanceRecordsByRange(
//        employeeId: Long,
//        startDate: LocalDate,
//        endDate: LocalDate
//    ): List<AttendanceRecord> {
//        return attendanceRecords
//            .filter { it.employeeId == employeeId && it.date in startDate..endDate }
//    }
//
//    override suspend fun insertRecords(records: List<AttendanceRecord>): InsertResult {
//        TODO("Not yet implemented")
//
////attendanceRecords.addAll(records)
////        return InsertResult.Success
//    }
//
//    override fun getPagedAttendanceSummaries(
//        month: Int,
//        year: Int,
//        range: ClosedRange<LocalDate>,
//        totalWorkingDays: Int,
//        pageSize: Int
//    ): Pager<Int, AttendanceRecordUI> {
//        TODO("Not yet implemented")
//    }
//
//}