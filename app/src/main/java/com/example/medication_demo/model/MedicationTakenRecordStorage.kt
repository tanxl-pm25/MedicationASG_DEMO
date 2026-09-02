package com.example.medication_demo.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicationTakenRecordStorage(
    val medicineId: Int,
    val date: String,
    val doseIndex: Int,
    val reminderTime: String,
    val takenTime: String,
    val dosageAmount: String,
    val dosageType: String
)