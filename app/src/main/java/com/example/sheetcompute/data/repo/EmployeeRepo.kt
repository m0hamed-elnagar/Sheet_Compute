package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.room.AppDatabase

class EmployeeRepo {
    private val database by lazy { AppDatabase.get() }

    private val employeeDao by lazy { database.employeeDao()}
    suspend fun getAllEmployeeIds(): List<Long> = employeeDao.getAllEmployeeIds()
    suspend fun insertEmployees(employees: List<EmployeeEntity>) = employeeDao.insertAll(employees)
}