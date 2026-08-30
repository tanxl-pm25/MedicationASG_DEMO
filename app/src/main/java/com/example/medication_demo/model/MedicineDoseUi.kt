package com.example.medication_demo.model

data class MedicineDoseUi(
    val time: String,
    val medicineName: String,
    val dosage: String,
    val status: DoseStatus,
    val extraText: String? = null
)