package com.example.sheetcompute.data.repo

import com.example.sheetcompute.data.entities.EmployeeEntity

class FakeEmployeeRepo : EmployeeRepoInterface {
    var employees = mutableListOf<EmployeeEntity>()
    var getByIdResult: EmployeeEntity? = null

    override suspend fun getAllEmployees(): List<EmployeeEntity> = employees
    override suspend fun getAllEmployeeIds(): List<Long> = employees.map { it.id }
    override suspend fun insertEmployees(employees: List<EmployeeEntity>) {
        this.employees.addAll(employees)
    }
    override suspend fun getEmployeeById(id: Long): EmployeeEntity? = getByIdResult ?: employees.find { it.id == id }
}

