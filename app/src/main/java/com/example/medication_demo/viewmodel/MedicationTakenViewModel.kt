package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationTakenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicationTakenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MedicationTakenUiState()
    )

    val uiState: StateFlow<MedicationTakenUiState> =
        _uiState.asStateFlow()

    fun setMedicationTaken(
        medicineName: String,
        dosage: String,
        scheduledTime: String,
        takenTime: String
    ) {
        _uiState.value = MedicationTakenUiState(
            medicineName = medicineName,
            dosage = dosage,
            scheduledTime = scheduledTime,
            takenTime = takenTime
        )
    }
}