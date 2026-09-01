package com.example.medication_demo.model

data class Medicine(
    val id: Int,
    val name: String,
    val quantity: String,
    val dosageAmount: String,
    val dosageType: String,
    val refillReminderEnabled: Boolean,
    val refillQuantity: String,
    val frequency: String,
    val reminderTimes: List<ReminderTimeUi>,
    val startDate: String,
    val notes: String,
    val reminderEnabled: Boolean = true,
    val presetImageRes: Int? = null,
    val galleryImageUri: String? = null
)