package com.example.medication_demo.viewmodel

import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.AppointmentUi
import com.example.medication_demo.model.EditAppointmentUiState
import com.example.medication_demo.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditAppointmentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        EditAppointmentUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun loadAppointment(appointmentId: Int) {
        val appointment = AppointmentRepository.getAppointmentById(
            appointmentId
        ) ?: run {
            _uiState.value = EditAppointmentUiState(
                isLoading = false
            )
            return
        }

        _uiState.value = EditAppointmentUiState(
            appointmentId = appointment.id,
            doctorName = appointment.doctor,
            appointmentName = appointment.appointmentName,
            date = appointment.date,
            time = appointment.time,
            location = appointment.location,
            purpose = appointment.purpose,
            notes = appointment.notes,
            reminderMinutesBefore =
                appointment.reminderMinutesBefore,
            isLoading = false
        )
    }

    fun updateDoctorName(value: String) {
        _uiState.value = _uiState.value.copy(
            doctorName = value,
            doctorNameError = false
        )
    }

    fun updateAppointmentName(value: String) {
        _uiState.value = _uiState.value.copy(
            appointmentName = value,
            appointmentNameError = false
        )
    }

    fun updateDate(value: String) {
        _uiState.value = _uiState.value.copy(
            date = value,
            dateError = false,
            scheduleError = null
        )
    }

    fun updateTime(value: String) {
        _uiState.value = _uiState.value.copy(
            time = value,
            timeError = false,
            scheduleError = null
        )
    }

    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(
            location = value,
            locationError = false
        )
    }

    fun updatePurpose(value: String) {
        _uiState.value = _uiState.value.copy(
            purpose = value
        )
    }

    fun updateNotes(value: String) {
        _uiState.value = _uiState.value.copy(
            notes = value
        )
    }

    fun updateReminderMinutes(value: Int?) {
        _uiState.value = _uiState.value.copy(
            reminderMinutesBefore = value
        )
    }

    fun saveChanges(): Boolean {
        val state = _uiState.value

        val doctorNameError = state.doctorName.isBlank()
        val appointmentNameError =
            state.appointmentName.isBlank()
        val dateError = state.date.isBlank()
        val timeError = state.time.isBlank()
        val locationError = state.location.isBlank()

        if (
            doctorNameError ||
            appointmentNameError ||
            dateError ||
            timeError ||
            locationError
        ) {
            _uiState.value = state.copy(
                doctorNameError = doctorNameError,
                appointmentNameError = appointmentNameError,
                dateError = dateError,
                timeError = timeError,
                locationError = locationError
            )
            return false
        }

        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy hh:mm a",
            Locale.ENGLISH
        )

        val appointmentDateTime = try {
            LocalDateTime.parse(
                "${state.date} ${state.time}",
                formatter
            )
        } catch (_: Exception) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please select a valid date and time."
            )
            return false
        }

        if (
            !appointmentDateTime.isAfter(
                getMalaysiaDate().atTime(getMalaysiaTime())
            )
        ) {
            _uiState.value = state.copy(
                scheduleError =
                    "Please choose a future date and time."
            )
            return false
        }

        val oldAppointment =
            AppointmentRepository.getAppointmentById(
                state.appointmentId
            )

        AppointmentRepository.updateAppointment(
            AppointmentUi(
                id = state.appointmentId,
                doctor = state.doctorName,
                appointmentName = state.appointmentName,
                date = state.date,
                time = state.time,
                location = state.location,
                purpose = state.purpose,
                notes = state.notes,
                reminderMinutesBefore =
                    state.reminderMinutesBefore,
                status = oldAppointment?.status
                    ?: com.example.medication_demo.model
                        .AppointmentStatus.UPCOMING,
                isGoing = oldAppointment?.isGoing ?: false
            )
        )

        return true
    }
}