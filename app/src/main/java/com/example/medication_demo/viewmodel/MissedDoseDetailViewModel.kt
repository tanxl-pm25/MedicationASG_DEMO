package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MissedDoseDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MissedDoseDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MissedDoseDetailUiState()
    )

    val uiState: StateFlow<MissedDoseDetailUiState> =
        _uiState.asStateFlow()

    fun setMissedMedication(
        medicineName: String,
        dosage: String,
        scheduledTime: String
    ) {
        _uiState.value = MissedDoseDetailUiState(
            medicineName = medicineName,
            dosage = dosage,
            scheduledTime = scheduledTime
        )
    }
}