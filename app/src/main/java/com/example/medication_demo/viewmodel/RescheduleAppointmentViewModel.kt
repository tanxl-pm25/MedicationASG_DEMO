package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.AppointmentUi
import com.example.medication_demo.model.RescheduleAppointmentUiState
import com.example.medication_demo.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RescheduleAppointmentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        RescheduleAppointmentUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun loadAppointment(appointmentId: Int) {
        val appointment = AppointmentRepository.getAppointmentById(
            appointmentId
        ) ?: run {
            _uiState.value = RescheduleAppointmentUiState(
                isLoading = false
            )
            return
        }

        _uiState.value = RescheduleAppointmentUiState(
            appointmentId = appointment.id,
            doctor = appointment.doctor,
            appointmentName = appointment.appointmentName,
            location = appointment.location,
            purpose = appointment.purpose,
            notes = appointment.notes,
            reminderMinutesBefore =
                appointment.reminderMinutesBefore,
            isLoading = false
        )
    }

    fun updateDate(value: String) {
        _uiState.value = _uiState.value.copy(
            newDate = value,
            dateError = false,
            scheduleError = null
        )
    }

    fun updateTime(value: String) {
        _uiState.value = _uiState.value.copy(
            newTime = value,
            timeError = false,
            scheduleError = null
        )
    }

    fun updateReminderMinutes(value: Int?) {
        _uiState.value = _uiState.value.copy(
            reminderMinutesBefore = value
        )
    }

    fun rescheduleAppointment(): Boolean {
        val state = _uiState.value

        val dateError = state.newDate.isBlank()
        val timeError = state.newTime.isBlank()

        if (dateError || timeError) {
            _uiState.value = state.copy(
                dateError = dateError,
                timeError = timeError
            )
            return false
        }

        val oldAppointment =
            AppointmentRepository.getAppointmentById(
                state.appointmentId
            ) ?: return false

        val isSameSchedule =
            state.newDate == oldAppointment.date &&
                    state.newTime == oldAppointment.time

        if (isSameSchedule) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please choose a different date or time."
            )
            return false
        }

        AppointmentRepository.markAsRescheduled(
            appointmentId = oldAppointment.id
        )

        AppointmentRepository.addAppointment(
            AppointmentUi(
                id = AppointmentRepository.getNextId(),
                doctor = oldAppointment.doctor,
                appointmentName =
                    oldAppointment.appointmentName,
                date = state.newDate,
                time = state.newTime,
                location = oldAppointment.location,
                purpose = oldAppointment.purpose,
                notes = oldAppointment.notes,
                reminderMinutesBefore =
                    state.reminderMinutesBefore
            )
        )

        return true
    }
}