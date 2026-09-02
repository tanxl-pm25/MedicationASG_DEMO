package com.example.medication_demo.model

import kotlinx.serialization.Serializable

@Serializable
data class ReminderTimeUi(
    val time: String,
    val minutes: String,
    val reminderOptionsEnabled: Boolean = false,
    val minutesError: String? = null
)