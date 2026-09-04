package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.model.AppointmentDetailsUiState
import com.example.medication_demo.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentDetailsViewModel(
    private val appointmentId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AppointmentDetailsUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AppointmentRepository.appointments.collect { appointments ->
                val appointment = appointments.find {
                    it.id == appointmentId
                }

                if (appointment != null) {
                    _uiState.value = AppointmentDetailsUiState(
                        appointmentId = appointment.id,
                        doctor = appointment.doctor,
                        appointmentName = appointment.appointmentName,
                        date = appointment.date,
                        time = appointment.time,
                        location = appointment.location,
                        purpose = appointment.purpose,
                        notes = appointment.notes,
                        status = appointment.status,
                        reminderMinutesBefore = appointment.reminderMinutesBefore,
                        isGoing = appointment.isGoing,
                        isLoading = false
                    )
                } else {
                    _uiState.value = AppointmentDetailsUiState(
                        appointmentId = appointmentId,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteAppointment() {
        AppointmentRepository.deleteAppointment(appointmentId)
    }
}

class AppointmentDetailsViewModelFactory(
    private val appointmentId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                AppointmentDetailsViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentDetailsViewModel(
                appointmentId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}