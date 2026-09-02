package com.example.medication_demo.model


import kotlinx.serialization.Serializable

@Serializable
enum class AppointmentStatus {
    UPCOMING,
    COMPLETED,
    MISSED,
    CANCELLED,
    RESCHEDULED
}

@Serializable
data class AppointmentUi(
    val id: Int,
    val doctor: String,
    val appointmentName: String,
    val date: String,
    val time: String,
    val location: String,
    val purpose: String,
    val notes: String,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val reminderMinutesBefore: Int? = 60,
    val isGoing: Boolean = false
)
