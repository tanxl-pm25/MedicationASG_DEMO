package com.example.medication_demo.model

import java.time.LocalDate

data class RescheduledDose(
    val medicineId: Int,
    val date: LocalDate,
    val originalTime: String,
    val newTime: String
)