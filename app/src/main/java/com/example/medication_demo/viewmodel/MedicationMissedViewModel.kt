package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationMissedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicationMissedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MedicationMissedUiState()
    )

    val uiState: StateFlow<MedicationMissedUiState> =
        _uiState.asStateFlow()

    fun setMissedMedication(
        medicineName: String,
        dosage: String,
        scheduledTime: String
    ) {
        _uiState.value = MedicationMissedUiState(
            medicineName = medicineName,
            dosage = dosage,
            scheduledTime = scheduledTime
        )
    }
}