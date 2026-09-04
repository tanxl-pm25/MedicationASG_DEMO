package com.example.medication_demo.model

data class AppointmentDetailsUiState(
    val appointmentId: Int = 0,
    val doctor: String = "",
    val appointmentName: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val purpose: String = "",
    val notes: String = "",
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val reminderMinutesBefore: Int? = null,
    val isGoing: Boolean = false,
    val isLoading: Boolean = true
)