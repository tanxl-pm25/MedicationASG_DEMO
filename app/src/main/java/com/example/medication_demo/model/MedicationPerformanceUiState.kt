package com.example.medication_demo.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth

data class MedicationPerformanceItem(
    val medicationName: String,
    val taken: Int,
    val total: Int,
    val missed: Int,
    val iconColor: Color
)

data class MedicationPerformanceUiState(
    val selectedMonth: YearMonth = YearMonth.of(2025, 5),

    val medications: List<MedicationPerformanceItem> = listOf(
        MedicationPerformanceItem(
            medicationName = "Vitamin D3 1000IU",
            taken = 30,
            total = 31,
            missed = 1,
            iconColor = Color(0xFF1976D2)
        )
    )
)