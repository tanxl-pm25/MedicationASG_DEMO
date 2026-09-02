package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MissedMedicine
import com.example.medication_demo.model.MissedMedicationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth

class MissedMedicationViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            MissedMedicationUiState()
        )

    val uiState: StateFlow<MissedMedicationUiState> =
        _uiState.asStateFlow()


    fun previousMonth() {

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    _uiState.value.selectedMonth.minusMonths(1)
            )

        updateMissedMedication()
    }


    fun nextMonth() {
        if (
            !_uiState.value.selectedMonth.isBefore(
                java.time.YearMonth.now()
            )
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    _uiState.value.selectedMonth.plusMonths(1)
            )

        updateMissedMedication()
    }


    fun selectMonth(
        year: Int,
        month: Int
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    YearMonth.of(year, month)
            )

        updateMissedMedication()
    }


    private fun updateMissedMedication() {

        when (_uiState.value.selectedMonth.monthValue) {

            // JUNE
            6 -> {

                _uiState.value =
                    _uiState.value.copy(
                        missedMedicines = listOf(

                            MissedMedicine(
                                day = "5",
                                month = "Jun 2026",
                                weekday = "Fri",
                                medicineName = "Vitamin D3 1000IU",
                                scheduledTime = "8:00 AM",
                                dosage = "1000 IU"
                            ),

                            MissedMedicine(
                                day = "16",
                                month = "Jun 2026",
                                weekday = "Tue",
                                medicineName = "Metformin 500mg",
                                scheduledTime = "8:00 AM",
                                dosage = "500 mg"
                            ),

                            MissedMedicine(
                                day = "24",
                                month = "Jun 2026",
                                weekday = "Wed",
                                medicineName = "Omega-3 1000mg",
                                scheduledTime = "1:00 PM",
                                dosage = "1000 mg"
                            )
                        )
                    )
            }


            // JULY
            7 -> {

                _uiState.value =
                    _uiState.value.copy(
                        missedMedicines = listOf(

                            MissedMedicine(
                                "3",
                                "Jul 2026",
                                "Thu",
                                "Vitamin D3 1000IU",
                                "8:00 AM",
                                "1000 IU"
                            ),

                            MissedMedicine(
                                "12",
                                "Jul 2026",
                                "Sat",
                                "Metformin 500mg",
                                "8:00 AM",
                                "500 mg"
                            ),

                            MissedMedicine(
                                "18",
                                "Jul 2026",
                                "Fri",
                                "Omega-3 1000mg",
                                "1:00 PM",
                                "1000 mg"
                            ),

                            MissedMedicine(
                                "21",
                                "Jul 2026",
                                "Mon",
                                "Metformin 500mg",
                                "8:00 AM",
                                "500 mg"
                            ),

                            MissedMedicine(
                                "25",
                                "Jul 2026",
                                "Fri",
                                "Omega-3 1000mg",
                                "1:00 PM",
                                "1000 mg"
                            ),

                            MissedMedicine(
                                "29",
                                "Jul 2026",
                                "Tue",
                                "Metformin 500mg",
                                "8:00 AM",
                                "500 mg"
                            )
                        )
                    )
            }


            // AUGUST
            8 -> {

                _uiState.value =
                    _uiState.value.copy(
                        missedMedicines = listOf(

                            MissedMedicine(
                                "7",
                                "Aug 2026",
                                "Fri",
                                "Metformin 500mg",
                                "8:00 AM",
                                "500 mg"
                            ),

                            MissedMedicine(
                                "20",
                                "Aug 2026",
                                "Thu",
                                "Omega-3 1000mg",
                                "1:00 PM",
                                "1000 mg"
                            )
                        )
                    )
            }


            else -> {

                _uiState.value =
                    _uiState.value.copy(
                        missedMedicines = emptyList()
                    )
            }
        }
    }
}