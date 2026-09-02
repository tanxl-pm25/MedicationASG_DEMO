package com.example.medication_demo.model

import java.time.LocalDate
import java.time.LocalTime

data class HistoryMedicineSource(
    val medicine: Medicine,
    val deletedDate: LocalDate? = null,
    val deletedTime: LocalTime? = null
)