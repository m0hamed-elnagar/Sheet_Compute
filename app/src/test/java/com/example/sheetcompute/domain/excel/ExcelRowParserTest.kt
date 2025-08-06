package com.example.sheetcompute.domain.excel

import com.example.sheetcompute.data.entities.AttendanceRecord
import com.example.sheetcompute.data.entities.EmployeeEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import org.junit.After
import org.junit.Before
import java.time.LocalTime
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ExcelRowParserMockKTest {

    private lateinit var row: Row
    private val workStartTime = LocalTime.of(9, 0)
    private val testDate = "2024-08-01"

    @Before
    fun setup() {
        row = mockk {
            every { rowNum } returns 0
            every { lastCellNum } returns 4
        }
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `malformed time returns Invalid time error`() {
        mockRow("1001", "John Doe", testDate, "25:70")
        assertError(ExcelRowParser.parseRow(row, workStartTime)) { error ->
            assertEquals("Invalid time", error.reason)
        }
    }

    // region Success Cases
    @Test
    fun `parse valid row returns Success`() {
        mockRow("1001", "John Doe", "2024-08-02", "09:15:00")

        val result = ExcelRowParser.parseRow(row, workStartTime)

        assertSuccess(result) { (employee, record) ->
            assertEquals(1001L, employee.id)
            assertEquals("John Doe", employee.name)
            assertEquals(15, record.tardyMinutes)
        }
    }


    @Test
    fun `parse valid row with numeric time returns Success`() {
        mockRow(
            id = "233",
            name = "John dickson",
            date = testDate,
            time = "0.5104" // 12:15:00
        )

        // When
        val result = ExcelRowParser.parseRow(row, workStartTime)

        assertSuccessResult(result) {
            assertEquals(194, record.tardyMinutes) // 9h15m in minutes
        }
    }

    @Test
    fun `empty ID cell returns Error`() {
        mockRowCells(
            idCell = mockEmptyCell(),
            name = "John Doe",
            date = testDate,
            time = "09:00"
        )

        // When
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Missing ID", error.reason)
        }

    }

    @Test
    fun `invalid date format returns Error`() {
        mockRowCells(
            id = "1001",
            name = "John Doe",
            date = "01-038-2024", // Wrong format
            time = "09:00"
        )

        val result = ExcelRowParser.parseRow(row, workStartTime)


        assertError(result) { error ->
            assertEquals("Invalid date", error.reason)
        }
    }

    @Test
    fun `malformed time value returns Error`() {
        mockRowCells(
            id = "1001",
            name = "John Doe",
            date = testDate,
            time = "25:70" // Invalid time
        )

        // When
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Invalid time", error.reason)
        }
    }

    @Test
    fun `empty name cell returns Error`() {
        mockRowCells(
            id = "1001",
            name = null,
            date = testDate,
            time = "09:00"
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Missing name", error.reason)
        }
    }

    @Test
    fun `empty date cell returns Error`() {
        mockRowCells(
            id = "1001",
            name = "John Doe",
            date = null,
            time = "09:00"
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Missing date", error.reason)
        }
    }

    @Test
    fun `empty time cell returns Error`() {
        mockRowCells(
            id = "1001",
            name = "John Doe",
            date = testDate,
            time = null
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Missing time", error.reason)
        }
    }

    @Test
    fun `non-numeric ID returns Error`() {
        mockRowCells(
            id = "abc",
            name = "John Doe",
            date = testDate,
            time = "09:00"
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertEquals("Invalid ID format", error.reason)
        }
    }

    @Test
    fun `time before work start returns zero tardy`() {
        mockRow("1001", "John Doe", testDate, "08:45:00")
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertSuccess(result) { (_, record) ->
            assertEquals(0, record.tardyMinutes)
        }
    }

    @Test
    fun `all fields blank returns Error`() {
        mockRowCells(
            idCell = mockEmptyCell(),
            name = null,
            date = null,
            time = null
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertError(result) { error ->
            assertTrue(error.reason.contains("Missing"))
        }
    }

    @Test
    fun `numeric cell for time returns Success`() {
        val mockStyle = mockk<CellStyle> {
            every { dataFormat } returns 0
            every { dataFormatString } returns "h:mm:ss"
        }

        val date = Date() // or a specific Date if needed

        val cell = mockk<Cell> {
            every { cellTypeEnum } returns CellType.NUMERIC
            every { numericCellValue } returns 0.5 // Excel decimal for 12:00 PM
            every { dateCellValue } returns date   // Required for POI date formatting
            every { cellStyle } returns mockStyle
        }
        mockRowCells(
            id = "1001",
            name = "John Doe",
            date = testDate,
            timeCell = cell
        )
        val result = ExcelRowParser.parseRow(row, workStartTime)
        assertSuccess(result) { (_, record) ->
            assertTrue(record.tardyMinutes > 0)
        }
    }

    // region Mock Builders
    private fun mockStringCell(value: String): Cell {
        val cell = mockk<Cell>()
        val richText = mockk<RichTextString> {
            every { string } returns value
        }

        every { cell.cellTypeEnum } returns CellType.STRING
        every { cell.stringCellValue } returns value
        every { cell.richStringCellValue } returns richText
        return cell
    }

    private fun mockEmptyCell(): Cell {
        val cell = mockk<Cell>()
        every { cell.cellTypeEnum } returns CellType.BLANK
        every { cell.toString() } returns ""
        return cell
    }

    private fun mockRowCells(
        id: String? = null,
        idCell: Cell? = null,
        name: String? = null,
        date: String? = null,
        time: String? = null,
        timeCell: Cell? = null
    ) {
        every { row.lastCellNum } returns 4
        every { row.getCell(0) } returns (idCell ?: id?.let { mockStringCell(it) })
        every { row.getCell(1) } returns (name?.let { mockStringCell(it) })
        every { row.getCell(2) } returns (date?.let { mockStringCell(it) })
        every { row.getCell(3) } returns (timeCell ?: time?.let { mockStringCell(it) })
    }
    // endregion

    // region Assertion Helpers
    private fun assertSuccessResult(
        result: ParseResult?,
        assertions: ParseResult.Success.() -> Unit
    ) {
        assertNotNull(result)
        assertTrue(result is ParseResult.Success)
        result.assertions()
    }

    private fun mockRow(
        id: String,
        name: String,
        date: String,
        time: String
    ) {
        every { row.getCell(0) } returns stringCell(id)
        every { row.getCell(1) } returns stringCell(name)
        every { row.getCell(2) } returns stringCell(date)
        every { row.getCell(3) } returns stringCell(time)
    }


    private fun stringCell(value: String): Cell = mockk {
        every { cellTypeEnum } returns CellType.STRING
        every { stringCellValue } returns value
        every { richStringCellValue } returns mockk {
            every { string } returns value
        }
    }

    private inline fun assertSuccess(
        result: ParseResult?,
        assertions: (Pair<EmployeeEntity, AttendanceRecord>) -> Unit
    ) {
        assertNotNull(result)
        val success = assertIs<ParseResult.Success>(result)
        assertions(success.employee to success.record)
    }

    private inline fun assertError(
        result: ParseResult?,
        assertions: (ParseResult.Error) -> Unit
    ) {
        assertNotNull(result)
        val error = assertIs<ParseResult.Error>(result)
        assertions(error)
    }
}
