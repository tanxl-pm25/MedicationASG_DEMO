package com.example.medication_demo.model

import java.time.YearMonth

data class MonthlyStatisticsUiState(
    val selectedMonth: YearMonth = YearMonth.of(2025, 5),
    val adherencePercentage: Int = 82,
    val adherenceMessage: String = "Well done! Keep it up.",
    val takenDoses: Int = 128,
    val missedDoses: Int = 28,
    val totalDoses: Int = 156,
    val chartValues: List<Int> = listOf(
        35, 48, 68, 55, 64, 72, 60, 92
    ),
    val chartLabels: List<String> = listOf(
        "1", "5", "10", "15", "20", "25", "30","35"
    )
)