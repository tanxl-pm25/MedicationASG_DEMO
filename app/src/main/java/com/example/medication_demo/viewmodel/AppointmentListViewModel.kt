package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.model.AppointmentListUiState
import com.example.medication_demo.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AppointmentListViewModel : ViewModel() {

    private val isUpcomingSelected = MutableStateFlow(true)

    private val _uiState = MutableStateFlow(AppointmentListUiState())
    val uiState: StateFlow<AppointmentListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                AppointmentRepository.appointments,
                isUpcomingSelected
            ) { appointments, upcomingSelected ->
                AppointmentListUiState(
                    isUpcomingSelected = upcomingSelected,
                    appointments = appointments
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun showUpcoming() { isUpcomingSelected.value = true }
    fun showHistory() { isUpcomingSelected.value = false }
}