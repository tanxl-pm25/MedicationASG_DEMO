package com.example.medication_demo.model

import java.time.LocalDate

data class MedicineScheduleSnapshot(
    val medicineId: Int,
    val effectiveDate: LocalDate,

    val name: String,
    val quantity: String,
    val dosageAmount: String,
    val dosageType: String,

    val frequency: String,
    val reminderTimes: List<ReminderTimeUi>,
    val startDate: String,

    val presetImageRes: Int?,
    val galleryImageUri: String?
)