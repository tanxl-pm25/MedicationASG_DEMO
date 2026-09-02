package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MonthlyStatisticsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth

class MonthlyStatisticsViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            MonthlyStatisticsUiState()
        )

    val uiState: StateFlow<MonthlyStatisticsUiState> =
        _uiState.asStateFlow()


    fun previousMonth() {

        val currentMonth =
            _uiState.value.selectedMonth

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    currentMonth.minusMonths(1)
            )

        updateStatistics()
    }




    fun nextMonth() {
        if (
            !_uiState.value.selectedMonth.isBefore(
                YearMonth.now()
            )
        ) {
            return
        }

        val currentMonth =
            _uiState.value.selectedMonth

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    currentMonth.plusMonths(1)
            )

        updateStatistics()
    }

    fun selectMonth(
        year: Int,
        month: Int
    ) {
        _uiState.value = _uiState.value.copy(
            selectedMonth = java.time.YearMonth.of(
                year,
                month
            )
        )

        updateStatistics()
    }


    private fun updateStatistics() {

        val month =
            _uiState.value.selectedMonth.monthValue

        when (month) {

            4 -> {

                _uiState.value =
                    _uiState.value.copy(

                        adherencePercentage = 76,

                        adherenceMessage =
                            "You're improving!",

                        takenDoses = 112,

                        missedDoses = 35,

                        totalDoses = 147,

                        chartValues = listOf(
                            40,
                            52,
                            61,
                            48,
                            70,
                            65,
                            75,
                            82
                        )
                    )
            }


            5 -> {

                _uiState.value =
                    _uiState.value.copy(

                        adherencePercentage = 82,

                        adherenceMessage =
                            "Well done! Keep it up.",

                        takenDoses = 128,

                        missedDoses = 28,

                        totalDoses = 156,

                        chartValues = listOf(
                            35,
                            48,
                            68,
                            55,
                            64,
                            72,
                            60,
                            92
                        )
                    )
            }


            6 -> {

                _uiState.value =
                    _uiState.value.copy(

                        adherencePercentage = 88,

                        adherenceMessage =
                            "Excellent progress!",

                        takenDoses = 142,

                        missedDoses = 19,

                        totalDoses = 161,

                        chartValues = listOf(
                            55,
                            60,
                            72,
                            68,
                            78,
                            82,
                            88,
                            95
                        )
                    )
            }
        }
    }
}