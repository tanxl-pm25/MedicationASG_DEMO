package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationMissedRecord
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.MonthlyStatisticsUiState
import com.example.medication_demo.utils.getMalaysiaDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth
import kotlin.math.roundToInt

class MonthlyStatisticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MonthlyStatisticsUiState()
    )

    val uiState: StateFlow<MonthlyStatisticsUiState> =
        _uiState.asStateFlow()

    private var takenRecords =
        emptyList<MedicationTakenRecord>()

    private var missedRecords =
        emptyList<MedicationMissedRecord>()

    fun updateRecords(
        taken: List<MedicationTakenRecord>,
        missed: List<MedicationMissedRecord>
    ) {
        takenRecords = taken
        missedRecords = missed
        updateStatistics()
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth
                .minusMonths(1)
        )

        updateStatistics()
    }

    fun nextMonth() {
        val currentMonth = YearMonth.from(
            getMalaysiaDate()
        )

        if (
            !_uiState.value.selectedMonth.isBefore(
                currentMonth
            )
        ) {
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth
                .plusMonths(1)
        )

        updateStatistics()
    }

    fun selectMonth(
        year: Int,
        month: Int
    ) {
        _uiState.value = _uiState.value.copy(
            selectedMonth = YearMonth.of(year, month)
        )

        updateStatistics()
    }

    private fun updateStatistics() {
        val selectedMonth = _uiState.value.selectedMonth

        val takenForMonth = takenRecords.filter {
            YearMonth.from(it.date) == selectedMonth
        }

        val missedForMonth = missedRecords.filter {
            YearMonth.from(it.date) == selectedMonth
        }

        val takenCount = takenForMonth.size
        val missedCount = missedForMonth.size
        val totalCount = takenCount + missedCount

        val percentage =
            if (totalCount == 0) {
                0
            } else {
                (
                        takenCount.toFloat() /
                                totalCount.toFloat() * 100
                        ).roundToInt()
            }

        val message = when {
            totalCount == 0 ->
                "No medication data yet."

            percentage >= 90 ->
                "Excellent progress!"

            percentage >= 75 ->
                "Well done! Keep it up."

            percentage >= 50 ->
                "You're improving!"

            else ->
                "Let's get back on track."
        }

        val daysPerBar =
            (selectedMonth.lengthOfMonth() + 7) / 8

        val chartRanges = List(8) { index ->
            val startDay = index * daysPerBar + 1

            val endDay = minOf(
                startDay + daysPerBar - 1,
                selectedMonth.lengthOfMonth()
            )

            startDay to endDay
        }

        val chartValues = chartRanges.map { range ->
            val takenForRange = takenForMonth.count {
                it.date.dayOfMonth in range.first..range.second
            }

            val missedForRange = missedForMonth.count {
                it.date.dayOfMonth in range.first..range.second
            }

            val totalForRange =
                takenForRange + missedForRange

            if (totalForRange == 0) {
                0
            } else {
                (
                        takenForRange.toFloat() /
                                totalForRange.toFloat() * 100
                        ).roundToInt()
            }
        }

        _uiState.value = _uiState.value.copy(
            adherencePercentage = percentage,
            adherenceMessage = message,
            takenDoses = takenCount,
            missedDoses = missedCount,
            totalDoses = totalCount,
            chartValues = chartValues,
            chartLabels = chartRanges.map {
                it.first.toString()
            }
        )
    }
}