package com.example.medication_demo.model

data class ReminderTimeUi(
    val time: String,
    val minutes: String,
    val reminderOptionsEnabled: Boolean = false,
    val minutesError: String? = null
)