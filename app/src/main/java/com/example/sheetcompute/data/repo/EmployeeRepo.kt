package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.room.daos.EmployeeDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepo  @Inject constructor(
    private val employeeDao: EmployeeDao,
){
    suspend fun getAllEmployees(): List<EmployeeEntity> = employeeDao.getAllEmployees()
    suspend fun getEmployees(query: String? = null): List<EmployeeEntity> { return employeeDao.getEmployees(query) }
    suspend fun getAllEmployeeIds(): List<Long> = employeeDao.getAllEmployeeIds()
    suspend fun insertEmployees(employees: List<EmployeeEntity>) = employeeDao.insertAll(employees)
    //get employee by id
    suspend fun getEmployeeById(id: Long): EmployeeEntity? {
        return employeeDao.getEmployeeById(id)
    }
}