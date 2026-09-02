package com.example.medication_demo.model

import kotlinx.serialization.Serializable

@Serializable
data class RescheduledDoseStorage(
    val medicineId: Int,
    val date: String,
    val doseIndex: Int,
    val originalTime: String,
    val newTime: String
)