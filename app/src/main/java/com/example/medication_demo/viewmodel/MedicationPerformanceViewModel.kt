package com.example.medication_demo.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationMissedRecord
import com.example.medication_demo.model.MedicationPerformanceItem
import com.example.medication_demo.model.MedicationPerformanceUiState
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.utils.getMalaysiaDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.YearMonth

class MedicationPerformanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MedicationPerformanceUiState()
    )

    val uiState: StateFlow<MedicationPerformanceUiState> =
        _uiState.asStateFlow()

    private var takenRecords =
        emptyList<MedicationTakenRecord>()

    private var missedRecords =
        emptyList<MedicationMissedRecord>()

    private var medicines =
        emptyList<Medicine>()

    fun updateRecords(
        taken: List<MedicationTakenRecord>,
        missed: List<MedicationMissedRecord>,
        medicineList: List<Medicine>
    ) {
        takenRecords = taken
        missedRecords = missed
        medicines = medicineList
        updateMedicationPerformance()
    }

    fun previousMonth() {
        _uiState.value = _uiState.value.copy(
            selectedMonth = _uiState.value.selectedMonth
                .minusMonths(1)
        )

        updateMedicationPerformance()
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

        updateMedicationPerformance()
    }

    private fun updateMedicationPerformance() {
        val selectedMonth = _uiState.value.selectedMonth

        val takenForMonth = takenRecords.filter {
            YearMonth.from(it.date) == selectedMonth
        }

        val missedForMonth = missedRecords.filter {
            YearMonth.from(it.date) == selectedMonth
        }

        val medicineIds = (
                takenForMonth.map { it.medicineId } +
                        missedForMonth.map { it.medicineId }
                ).distinct()

        val colors = listOf(
            Color(0xFF1976D2),
            Color(0xFF159447),
            Color(0xFFFFB300),
            Color(0xFF8E44AD)
        )

        val items = medicineIds.mapIndexed { index, medicineId ->
            val medicine = medicines.firstOrNull {
                it.id == medicineId
            }

            val takenCount = takenForMonth.count {
                it.medicineId == medicineId
            }

            val missedCount = missedForMonth.count {
                it.medicineId == medicineId
            }

            MedicationPerformanceItem(
                medicationName = medicine?.name
                    ?: "Medication",
                taken = takenCount,
                missed = missedCount,
                total = takenCount + missedCount,
                iconColor = colors[index % colors.size],
                presetImageRes = medicine?.presetImageRes,
                galleryImageUri = medicine?.galleryImageUri
            )
        }

        _uiState.value = _uiState.value.copy(
            medications = items
        )
    }
}