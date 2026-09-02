package com.example.medication_demo.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicineScheduleSnapshotStorage(
    val medicineId: Int,
    val effectiveDate: String,

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