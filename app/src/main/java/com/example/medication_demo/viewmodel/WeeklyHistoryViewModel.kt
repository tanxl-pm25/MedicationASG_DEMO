package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WeeklyHistoryViewModel : ViewModel() {

    private val _selectedStartDate =
        MutableStateFlow(LocalDate.of(2025, 5, 10))
    val selectedStartDate: StateFlow<LocalDate> =
        _selectedStartDate.asStateFlow()

    private val _selectedEndDate =
        MutableStateFlow(LocalDate.of(2025, 5, 16))
    val selectedEndDate: StateFlow<LocalDate> =
        _selectedEndDate.asStateFlow()

    private val _showDateRangePicker =
        MutableStateFlow(false)
    val showDateRangePicker: StateFlow<Boolean> =
        _showDateRangePicker.asStateFlow()

    private val _dateRangeError =
        MutableStateFlow<String?>(null)
    val dateRangeError: StateFlow<String?> =
        _dateRangeError.asStateFlow()


    fun openDateRangePicker() {
        _dateRangeError.value = null
        _showDateRangePicker.value = true
    }


    fun closeDateRangePicker() {
        _showDateRangePicker.value = false
        _dateRangeError.value = null
    }


    fun confirmDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ) {

        val selectedDayCount =
            ChronoUnit.DAYS.between(
                startDate,
                endDate
            ) + 1

        if (selectedDayCount == 7L) {

            _selectedStartDate.value = startDate
            _selectedEndDate.value = endDate

            _dateRangeError.value = null
            _showDateRangePicker.value = false

        } else {

            _dateRangeError.value =
                "Please select exactly 7 days."
        }
    }
}