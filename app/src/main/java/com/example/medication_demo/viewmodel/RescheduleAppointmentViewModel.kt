package com.example.medication_demo.viewmodel

import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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

    fun rescheduleAppointment(): AppointmentUi? {
        val state = _uiState.value

        val dateError = state.newDate.isBlank()
        val timeError = state.newTime.isBlank()

        if (dateError || timeError) {
            _uiState.value = state.copy(
                dateError = dateError,
                timeError = timeError
            )
            return null
        }

        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy hh:mm a",
            Locale.ENGLISH
        )

        val newAppointmentDateTime = try {
            LocalDateTime.parse(
                "${state.newDate} ${state.newTime}",
                formatter
            )
        } catch (_: Exception) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please select a valid date and time."
            )
            return null
        }

        val nowDateTime = getMalaysiaDate()
            .atTime(getMalaysiaTime())

        if (!newAppointmentDateTime.isAfter(nowDateTime)) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please choose a future date and time."
            )
            return null
        }

        val oldAppointment =
            AppointmentRepository.getAppointmentById(
                state.appointmentId
            ) ?: return null

        val isSameSchedule =
            state.newDate == oldAppointment.date &&
                    state.newTime == oldAppointment.time

        if (isSameSchedule) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please choose a different date or time."
            )
            return null
        }

        AppointmentRepository.markAsRescheduled(
            appointmentId = oldAppointment.id
        )

        val newAppointment = AppointmentUi(
            id = AppointmentRepository.getNextId(),
            doctor = oldAppointment.doctor,
            appointmentName = oldAppointment.appointmentName,
            date = state.newDate,
            time = state.newTime,
            location = oldAppointment.location,
            purpose = oldAppointment.purpose,
            notes = oldAppointment.notes,
            reminderMinutesBefore = state.reminderMinutesBefore
        )

        AppointmentRepository.addAppointment(newAppointment)

        return newAppointment
    }
}