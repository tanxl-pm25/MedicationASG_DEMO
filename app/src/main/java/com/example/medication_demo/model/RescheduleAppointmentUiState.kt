package com.example.medication_demo.model

data class RescheduleAppointmentUiState(
    val appointmentId: Int = 0,
    val doctor: String = "",
    val appointmentName: String = "",
    val location: String = "",
    val purpose: String = "",
    val notes: String = "",

    val newDate: String = "",
    val newTime: String = "",
    val reminderMinutesBefore: Int? = 60,

    val dateError: Boolean = false,
    val timeError: Boolean = false,
    val scheduleError: String? = null,
    val isLoading: Boolean = true
)