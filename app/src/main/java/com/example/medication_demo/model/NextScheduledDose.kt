package com.example.medication_demo.model

import java.time.LocalDate
import java.time.LocalTime

data class NextScheduledDose(
    val doseDate: LocalDate,
    val doseIndex: Int,
    val scheduledTime: String,
    val scheduledLocalTime: LocalTime
)