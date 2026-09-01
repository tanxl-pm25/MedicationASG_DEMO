package com.example.medication_demo.model

import java.time.LocalDate

data class RescheduledDose(
    val medicineId: Int,
    val date: LocalDate,
    val doseIndex: Int,
    val originalTime: String,
    val newTime: String
)