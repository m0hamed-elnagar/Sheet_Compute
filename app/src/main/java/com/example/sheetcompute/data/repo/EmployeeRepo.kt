package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.local.room.daos.EmployeeDao
import javax.inject.Inject
import javax.inject.Singleton
interface EmployeeRepoInterface {
    suspend fun getAllEmployees(): List<EmployeeEntity>
    suspend fun getAllEmployeeIds(): List<Long>
    suspend fun insertEmployees(employees: List<EmployeeEntity>)
    suspend fun getEmployeeById(id: Long): EmployeeEntity?

}

@Singleton
class EmployeeRepo  @Inject constructor(
    private val employeeDao: EmployeeDao,
): EmployeeRepoInterface {
   override suspend fun getAllEmployees(): List<EmployeeEntity> = employeeDao.getAllEmployees()
  override  suspend fun getAllEmployeeIds(): List<Long> = employeeDao.getAllEmployeeIds()
  override  suspend fun insertEmployees(employees: List<EmployeeEntity>) = employeeDao.insertAll(employees)
    //get employee by id
  override  suspend fun getEmployeeById(id: Long): EmployeeEntity? {
        return employeeDao.getEmployeeById(id)
    }
}