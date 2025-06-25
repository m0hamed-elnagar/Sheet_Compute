package com.example.sheetcompute.domain.repo

import com.example.sheetcompute.entities.EmployeeEntity
import com.example.sheetcompute.domain.gateways.database.roomDB.AppDatabase

class EmployeeRepo {
    private val database by lazy { AppDatabase.get() }

    private val employeeDao by lazy { database.employeeDao()}
    suspend fun getAllEmployeeIds(): List<Int> = employeeDao.getAllEmployeeIds()
    suspend fun insertEmployees(employees: List<EmployeeEntity>) = employeeDao.insertAll(employees)
}