//package com.example.sheetcompute.data.entities
//
//import java.time.LocalDate
//
//object DummyAttendanceData2 {
//    val employeeAttendanceRecords = listOf(
//        EmployeeAttendanceRecord(1, "1", 540, LocalDate.of(2025, 5, 1), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(2, "1", 600, LocalDate.of(2025, 5, 2), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(3, "1", 570, LocalDate.of(2025, 5, 3), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(4, "1", 540, LocalDate.of(2025, 5, 4), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(5, "1", 600, LocalDate.of(2025, 5, 5), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(6, "1", 570, LocalDate.of(2025, 5, 6), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(7, "1", 540, LocalDate.of(2025, 5, 7), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(8, "1", 600, LocalDate.of(2025, 5, 8), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(9, "1", 570, LocalDate.of(2025, 5, 9), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(10, "1", 540, LocalDate.of(2025, 5, 10), 0L, AttendanceStatus.PRESENT), // 09:00
//        EmployeeAttendanceRecord(11, "1", 600, LocalDate.of(2025, 5, 11), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(12, "1", 570, LocalDate.of(2025, 5, 12), 30L, AttendanceStatus.PRESENT), // 09:30
//        EmployeeAttendanceRecord(13, "1", 540, LocalDate.of(2025, 5, 13), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(14, "1", 600, LocalDate.of(2025, 5, 14), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(15, "1", 570, LocalDate.of(2025, 5, 15), 30L, AttendanceStatus.PRESENT), // 09:30
//        EmployeeAttendanceRecord(16, "1", 540, LocalDate.of(2025, 5, 16), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(17, "1", 600, LocalDate.of(2025, 5, 17), 60L, AttendanceStatus.LATE),   // 10:00
//
//        EmployeeAttendanceRecord(18, "2", 570, LocalDate.of(2025, 5, 1), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(19, "2", 540, LocalDate.of(2025, 5, 2), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(20, "2", 600, LocalDate.of(2025, 5, 3), 60L, AttendanceStatus.LATE),     // 10:00
//
//        EmployeeAttendanceRecord(21, "2", 570, LocalDate.of(2025, 5, 7), 30L, AttendanceStatus.PRESENT),  // 09:30
//
//        EmployeeAttendanceRecord(22, "2", 540, LocalDate.of(2025, 5, 1), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(23, "2", 600, LocalDate.of(2025, 5, 2), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(24, "2", 570, LocalDate.of(2025, 5, 3), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(25, "2", 540, LocalDate.of(2025, 5, 4), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(26, "2", 600, LocalDate.of(2025, 5, 5), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(27, "2", 570, LocalDate.of(2025, 5, 6), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(28, "2", 540, LocalDate.of(2025, 5, 7), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(29, "2", 600, LocalDate.of(2025, 5, 8), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(30, "2", 570, LocalDate.of(2025, 5, 9), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(31, "2", 540, LocalDate.of(2025, 5, 10), 0L, AttendanceStatus.PRESENT), // 09:00
//        EmployeeAttendanceRecord(32, "2", 600, LocalDate.of(2025, 5, 11), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(33, "2", 570, LocalDate.of(2025, 5, 12), 30L, AttendanceStatus.PRESENT), // 09:30
//        EmployeeAttendanceRecord(34, "2", 540, LocalDate.of(2025, 5, 13), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(35, "2", 600, LocalDate.of(2025, 5, 14), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(36, "2", 570, LocalDate.of(2025, 5, 15), 30L, AttendanceStatus.PRESENT), // 09:30
//        EmployeeAttendanceRecord(37, "2", 540, LocalDate.of(2025, 5, 16), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(38, "2", 600, LocalDate.of(2025, 5, 17), 60L, AttendanceStatus.LATE),   // 10:00
//
//        EmployeeAttendanceRecord(39, "3", 570, LocalDate.of(2025, 5, 1), 30L, AttendanceStatus.ABSENT),  // 09:30)}
//        EmployeeAttendanceRecord(40, "3", 540, LocalDate.of(2025, 5, 2), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(41, "3", 600, LocalDate.of(2025, 5, 3), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(42, "3", 570, LocalDate.of(2025, 5, 4), 30L, AttendanceStatus.PRESENT),  // 09:30
//        EmployeeAttendanceRecord(43, "3", 540, LocalDate.of(2025, 5, 5), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(44, "3", 600, LocalDate.of(2025, 5, 6), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(45, "3", 570, LocalDate.of(2025, 5, 7), 30L, AttendanceStatus.ABSENT),  // 09:30
//        EmployeeAttendanceRecord(46, "3", 540, LocalDate.of(2025, 5, 8), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(47, "3", 600, LocalDate.of(2025, 5, 9), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(48, "3", 570, LocalDate.of(2025, 5, 10), 30L, AttendanceStatus.ABSENT),  // 09:30
//        EmployeeAttendanceRecord(49, "3", 540, LocalDate.of(2025, 5, 11), 0L, AttendanceStatus.PRESENT), // 09:00
//        EmployeeAttendanceRecord(50, "3", 600, LocalDate.of(2025, 5, 12), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(51, "3", 570, LocalDate.of(2025, 5, 13), 30L, AttendanceStatus.ABSENT), // 09:30
//        EmployeeAttendanceRecord(52, "3", 540, LocalDate.of(2025, 5, 14), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(53, "3", 600, LocalDate.of(2025, 5, 15), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(54, "3", 570, LocalDate.of(2025, 5, 16), 30L, AttendanceStatus.ABSENT), // 09:30
//        EmployeeAttendanceRecord(55, "3", 540, LocalDate.of(2025, 5, 17), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(56, "3", 600, LocalDate.of(2025, 5, 18), 60L, AttendanceStatus.LATE),   // 10:00
//        EmployeeAttendanceRecord(57, "3", 570, LocalDate.of(2025, 5, 19), 30L, AttendanceStatus.ABSENT), // 09:30
//
//        EmployeeAttendanceRecord(58, "4", 540, LocalDate.of(2025, 5, 1), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(59, "4", 600, LocalDate.of(2025, 5, 2), 60L, AttendanceStatus.LATE),     // 10:00// )}
//        EmployeeAttendanceRecord(60, "4", 570, LocalDate.of(2025, 4, 9), 30L, AttendanceStatus.EXTRA_DAY), // 09:30, Friday
//        EmployeeAttendanceRecord(61, "4", 540, LocalDate.of(2025, 4, 10), 0L, AttendanceStatus.PRESENT),  // 09:00
//        EmployeeAttendanceRecord(62, "4", 600, LocalDate.of(2025, 1, 11), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(63, "4", 570, LocalDate.of(2025, 1, 12), 30L, AttendanceStatus.PRESENT), // 09:30
//        EmployeeAttendanceRecord(64, "4", 540, LocalDate.of(2025, 1, 13), 0L, AttendanceStatus.PRESENT),   // 09:00
//        EmployeeAttendanceRecord(65, "4", 600, LocalDate.of(2025, 2, 14), 60L, AttendanceStatus.LATE),     // 10:00
//        EmployeeAttendanceRecord(66, "4", 570, LocalDate.of(2025, 2, 15), 30L, AttendanceStatus.PRESENT), // 09:30
//
//
//    )}