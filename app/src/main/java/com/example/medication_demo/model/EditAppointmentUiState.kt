package com.example.medication_demo.model

data class EditAppointmentUiState(
    val appointmentId: Int = 0,
    val doctorName: String = "",
    val appointmentName: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val purpose: String = "",
    val notes: String = "",
    val reminderMinutesBefore: Int? = 60,

    val doctorNameError: Boolean = false,
    val appointmentNameError: Boolean = false,
    val dateError: Boolean = false,
    val timeError: Boolean = false,
    val locationError: Boolean = false,

    val isLoading: Boolean = true
)