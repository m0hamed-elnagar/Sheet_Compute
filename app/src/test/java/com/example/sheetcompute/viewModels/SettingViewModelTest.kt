package com.example.sheetcompute.viewModels

import com.example.sheetcompute.data.local.PreferencesGateway
import com.example.sheetcompute.ui.features.settingFragment.SettingViewmodel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalTime


class SettingViewModelTest {
    private lateinit var preferencesGateway: PreferencesGateway
    private lateinit var viewModel: SettingViewmodel

    @Before
    fun setUp() {
        preferencesGateway = mockk(relaxed = true)
        every { preferencesGateway.getWorkStartTime() } returns LocalTime.of(8, 0)
        every { preferencesGateway.getMonthStartDay() } returns 1
        viewModel = SettingViewmodel(preferencesGateway)
    }

    @Test
    fun `setWorkStartTime updates state`() {
        val newTime = LocalTime.of(9, 30)
        viewModel.setWorkStartTime(newTime)
        assertEquals(newTime, viewModel.workStartTime.value)
    }

    @Test
    fun `setMonthStartDay updates state`() {
        val newDay = 15
        viewModel.setMonthStartDay(newDay)
        assertEquals(newDay, viewModel.monthStartDay.value)
    }

    @Test
    fun `saveSettings calls preferencesGateway methods`() {
        val newTime = LocalTime.of(7, 45)
        val newDay = 10
        viewModel.setWorkStartTime(newTime)
        viewModel.setMonthStartDay(newDay)
        viewModel.saveSettings()
        verify { preferencesGateway.saveWorkStartTime(newTime) }
        verify { preferencesGateway.setMonthStartDay(newDay) }
    }
}