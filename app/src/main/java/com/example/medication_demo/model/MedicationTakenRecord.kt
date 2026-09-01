package com.example.medication_demo.model

import java.time.LocalDate

data class MedicationTakenRecord(
    val medicineId: Int,
    val date: LocalDate,
    val doseIndex: Int,
    val reminderTime: String,
    val takenTime: String,
    val dosageAmount: String,
    val dosageType: String
)