package com.example.medication_demo.model

import java.time.LocalDate

data class ArchivedMedicine(
    val medicine: Medicine,
    val deletedDate: LocalDate
)