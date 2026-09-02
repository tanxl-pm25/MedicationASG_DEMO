package com.example.medication_demo.viewmodel


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationPerformanceItem
import com.example.medication_demo.model.MedicationPerformanceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth

class MedicationPerformanceViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            MedicationPerformanceUiState()
        )

    val uiState: StateFlow<MedicationPerformanceUiState> =
        _uiState.asStateFlow()


    fun previousMonth() {

        val currentMonth =
            _uiState.value.selectedMonth

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    currentMonth.minusMonths(1)
            )

        updateMedicationPerformance()
    }


    fun nextMonth() {

        if (
            !_uiState.value.selectedMonth.isBefore(
                java.time.YearMonth.now()
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

        updateMedicationPerformance()
    }


    fun selectMonth(
        year: Int,
        month: Int
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    YearMonth.of(
                        year,
                        month
                    )
            )

        updateMedicationPerformance()
    }


    private fun updateMedicationPerformance() {

        val month =
            _uiState.value.selectedMonth.monthValue

        when (month) {

            4 -> {
                _uiState.value =
                    _uiState.value.copy(
                        medications = listOf(
                            MedicationPerformanceItem(
                                medicationName =
                                    "Vitamin D3 1000IU",
                                taken = 27,
                                total = 30,
                                missed = 3,
                                iconColor =
                                    Color(0xFF1976D2)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Metformin 500mg",
                                taken = 55,
                                total = 60,
                                missed = 5,
                                iconColor =
                                    Color(0xFFBFC2C4)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Omega-3 1000mg",
                                taken = 25,
                                total = 30,
                                missed = 5,
                                iconColor =
                                    Color(0xFFFFB300)
                            )
                        )
                    )
            }


            5 -> {
                _uiState.value =
                    _uiState.value.copy(
                        medications = listOf(
                            MedicationPerformanceItem(
                                medicationName =
                                    "Vitamin D3 1000IU",
                                taken = 30,
                                total = 31,
                                missed = 1,
                                iconColor =
                                    Color(0xFF1976D2)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Metformin 500mg",
                                taken = 60,
                                total = 62,
                                missed = 2,
                                iconColor =
                                    Color(0xFFBFC2C4)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Omega-3 1000mg",
                                taken = 28,
                                total = 30,
                                missed = 2,
                                iconColor =
                                    Color(0xFFFFB300)
                            )
                        )
                    )
            }


            6 -> {
                _uiState.value =
                    _uiState.value.copy(
                        medications = listOf(
                            MedicationPerformanceItem(
                                medicationName =
                                    "Vitamin D3 1000IU",
                                taken = 30,
                                total = 30,
                                missed = 0,
                                iconColor =
                                    Color(0xFF1976D2)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Metformin 500mg",
                                taken = 58,
                                total = 60,
                                missed = 2,
                                iconColor =
                                    Color(0xFFBFC2C4)
                            ),

                            MedicationPerformanceItem(
                                medicationName =
                                    "Omega-3 1000mg",
                                taken = 29,
                                total = 30,
                                missed = 1,
                                iconColor =
                                    Color(0xFFFFB300)
                            )
                        )
                    )
            }
        }
    }
}