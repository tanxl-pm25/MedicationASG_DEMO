package com.example.medication_demo.model

import java.time.LocalDate

data class CalendarDayUi(
    val date: LocalDate?,
    val status: DoseStatus? = null
)