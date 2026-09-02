package com.example.medication_demo.model

data class MedicationMissedUiState(
    val medicineName: String = "Metformin",
    val dosage: String = "1 Tablet",
    val scheduledTime: String = "08:30 AM",
    val message: String = "Don't worry, we'll remind you next time."
)