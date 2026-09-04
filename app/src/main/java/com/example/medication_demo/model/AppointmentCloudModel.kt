package com.example.medication_demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentCloudModel(
    @SerialName("user_id")
    val userId: String,

    val id: Int,

    val doctor: String,

    @SerialName("appointment_name")
    val appointmentName: String,

    val date: String,

    val time: String,

    val location: String,

    val purpose: String,

    val notes: String,

    val status: AppointmentStatus,

    @SerialName("reminder_minutes_before")
    val reminderMinutesBefore: Int?,

    @SerialName("is_going")
    val isGoing: Boolean
)

fun AppointmentCloudModel.toAppointmentUi(): AppointmentUi {
    return AppointmentUi(
        id = id,
        doctor = doctor,
        appointmentName = appointmentName,
        date = date,
        time = time,
        location = location,
        purpose = purpose,
        notes = notes,
        status = status,
        reminderMinutesBefore = reminderMinutesBefore,
        isGoing = isGoing
    )
}