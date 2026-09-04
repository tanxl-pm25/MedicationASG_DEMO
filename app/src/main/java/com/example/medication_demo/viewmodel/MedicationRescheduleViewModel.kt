package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.RescheduleMedicationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RescheduleMedicationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        RescheduleMedicationUiState()
    )

    val uiState: StateFlow<RescheduleMedicationUiState> =
        _uiState.asStateFlow()

    fun updateTime(value: String) {
        _uiState.value = _uiState.value.copy(
            newTime = value
        )
    }

    fun reset() {
        _uiState.value =
            RescheduleMedicationUiState()
    }

    fun confirmReschedule() {
        // 下一步连接 MedicineViewModel、SharedPreferences 和 reminder scheduler。
    }
}