package com.example.medication_demo.model

import java.time.YearMonth
import java.time.ZoneId

data class MonthlyStatisticsUiState(
    val selectedMonth: YearMonth =
        YearMonth.now(
            ZoneId.of("Asia/Kuala_Lumpur")
        ),

    val adherencePercentage: Int = 0,
    val adherenceMessage: String = "No medication data yet.",
    val takenDoses: Int = 0,
    val missedDoses: Int = 0,
    val totalDoses: Int = 0,
    val chartValues: List<Int> = List(8) { 0 },
    val chartLabels: List<String> = List(8) { "" }
)