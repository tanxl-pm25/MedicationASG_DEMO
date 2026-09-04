package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationMissedRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MissedMedicine
import com.example.medication_demo.model.MissedMedicationUiState
import com.example.medication_demo.utils.getMalaysiaDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MissedMedicationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MissedMedicationUiState()
    )

    val uiState: StateFlow<MissedMedicationUiState> =
        _uiState.asStateFlow()

    private var missedRecords =
        emptyList<MedicationMissedRecord>()

    private var medicines =
        emptyList<Medicine>()

    fun updateRecords(
        missed: List<MedicationMissedRecord>,
        medicineList: List<Medicine>
    ) {
        missedRecords = missed
        medicines = medicineList
        updateMissedMedication()
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth
                .minusMonths(1)
        )

        updateMissedMedication()
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

        updateMissedMedication()
    }

    private fun updateMissedMedication() {
        val selectedMonth = _uiState.value.selectedMonth

        val monthFormatter = DateTimeFormatter.ofPattern(
            "MMM yyyy",
            Locale.ENGLISH
        )

        val weekdayFormatter = DateTimeFormatter.ofPattern(
            "EEE",
            Locale.ENGLISH
        )

        val list = missedRecords
            .filter { record ->
                YearMonth.from(record.date) == selectedMonth
            }
            .sortedByDescending { record ->
                record.date
            }
            .map { record ->
                val medicine = medicines.firstOrNull {
                    it.id == record.medicineId
                }

                MissedMedicine(
                    day = record.date.dayOfMonth.toString(),
                    month = record.date.format(monthFormatter),
                    weekday = record.date.format(weekdayFormatter),
                    medicineName = medicine?.name
                        ?: "Medication",
                    scheduledTime = record.reminderTime,
                    dosage =
                        "${record.dosageAmount} ${record.dosageType}"
                )
            }

        _uiState.value = _uiState.value.copy(
            missedMedicines = list
        )
    }
}