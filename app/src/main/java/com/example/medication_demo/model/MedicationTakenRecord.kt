package com.example.medication_demo.model

import java.time.LocalDate

data class MedicationTakenRecord(
    val medicineId: Int,
    val date: LocalDate,
    val reminderTime: String
)