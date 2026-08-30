package com.example.medication_demo.model

data class NextMedicineDose(
    val medicineId: Int,
    val medicineName: String,
    val dosage: String,
    val reminderTime: String,
    val originalTime: String,
    val status: DoseStatus
)