package com.example.medication_demo.model

data class MedicationTakenUiState(
    val medicineName: String = "Vitamin D3",
    val dosage: String = "1 Tablet",
    val scheduledTime: String = "08:00 AM",
    val takenTime: String = "08:00 AM",
    val encouragementMessage: String = "Great! Keep it up!"
)