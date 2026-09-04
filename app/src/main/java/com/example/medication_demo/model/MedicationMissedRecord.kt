package com.example.medication_demo.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

data class MedicationMissedRecord(
    val medicineId: Int,
    val date: LocalDate,
    val doseIndex: Int,
    val reminderTime: String,
    val dosageAmount: String,
    val dosageType: String
)

