package com.example.sheetcompute.viewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.example.sheetcompute.MainDispatcherRule
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.data.repo.EmployeeRepo
import com.example.sheetcompute.domain.excel.ExcelImporter
import com.example.sheetcompute.ui.features.attendanceHistory.searchHistory.SearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    // Dispatcher Rule to control coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Instant task rule to test LiveData if used
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var repository: EmployeeRepo
    private lateinit var excelImporter: ExcelImporter

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        excelImporter = mockk(relaxed = true)
        viewModel = SearchViewModel(repository, excelImporter)
    }

    @Test
    fun `searchQuery filters employees correctly`() = runTest {
        val allEmployees = listOf(
            EmployeeEntity(1, "Mohamed", "Engineer"),
            EmployeeEntity(2, "Ahmed", "Designer"),
            EmployeeEntity(3, "Mona", "HR")
        )
        coEvery { repository.getAllEmployees() } returns allEmployees

        viewModel.refreshData()
        advanceUntilIdle()
        viewModel.setSearchQuery("mo")

        viewModel.filteredEmployees.test {
            val filtered = awaitItem()
            assertThat(filtered).hasSize(2)
            assertThat(filtered.map { it.name }).containsExactly("Mohamed", "Mona")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllEmployees emits correct data`() = runTest {
        // Arrange
        val mockEmployees = listOf(
            EmployeeEntity(1, "Ali", "Manager"),
            EmployeeEntity(2, "Sara", "Accountant")
        )
        coEvery { repository.getAllEmployees() } returns mockEmployees

        // Act
        viewModel.refreshData()
   advanceUntilIdle() // Ensure coroutine launched inside ViewModel is executed

        // Assert
        viewModel.employees.test {
            val result = awaitItem()
            assertThat(result).isEqualTo(mockEmployees)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty searchQuery returns all employees`() = runTest {
        val employees = listOf(
            EmployeeEntity(1, "Ali", "Manager"),
            EmployeeEntity(2, "Sara", "Accountant")
        )
        coEvery { repository.getAllEmployees() } returns employees

        viewModel.refreshData()
        advanceUntilIdle()
        viewModel.setSearchQuery("")

        viewModel.employees.test {
            val result = awaitItem()
            assertThat(result).isEqualTo(employees)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuery with no match returns empty list`() = runTest {
        val employees = listOf(
            EmployeeEntity(1, "Ali", "Manager"),
            EmployeeEntity(2, "Sara", "Accountant")
        )
        coEvery { repository.getAllEmployees() } returns employees

        viewModel.refreshData()
        advanceUntilIdle()
        viewModel.setSearchQuery("xyz") // no match

        viewModel.filteredEmployees.test {
            val result = awaitItem()
            assertThat(result).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuery is case insensitive`() = runTest {
        val employees = listOf(
            EmployeeEntity(1, "Mohamed", "Engineer"),
            EmployeeEntity(2, "Mona", "HR")
        )
        coEvery { repository.getAllEmployees() } returns employees

        viewModel.refreshData()
        advanceUntilIdle()
        viewModel.setSearchQuery("MO")

        viewModel.employees.test {
            val result = awaitItem()
            assertThat(result).hasSize(2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calling refreshData multiple times emits updated data`() = runTest {
        val firstList = listOf(EmployeeEntity(1, "Ali", "Manager"))
        val secondList = listOf(EmployeeEntity(2, "Sara", "Accountant"))

        coEvery { repository.getAllEmployees() } returnsMany listOf(firstList, secondList)

        viewModel.refreshData()
        advanceUntilIdle()
        val first = viewModel.employees.value

        viewModel.refreshData()
        advanceUntilIdle()
        val second = viewModel.employees.value
        assertThat(first).isEqualTo(firstList)
        assertThat(second).isEqualTo(secondList)

    }

}
