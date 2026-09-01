package com.example.medication_demo.model

import java.time.LocalDate

data class MedicineDailyHistoryUi(
    val date: LocalDate,
    val frequency: String,
    val doses: List<MedicineDoseUi>,
    val takenCount: Int,
    val missingCount: Int
)