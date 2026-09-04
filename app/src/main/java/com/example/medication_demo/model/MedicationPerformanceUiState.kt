package com.example.medication_demo.model

import androidx.compose.ui.graphics.Color
import java.time.YearMonth
import java.time.ZoneId

data class MedicationPerformanceItem(
    val medicationName: String,
    val taken: Int,
    val total: Int,
    val missed: Int,
    val iconColor: Color,
    val presetImageRes: Int? = null,
    val galleryImageUri: String? = null
)

data class MedicationPerformanceUiState(
    val selectedMonth: YearMonth =
        YearMonth.now(
            ZoneId.of("Asia/Kuala_Lumpur")
        ),

    val medications: List<MedicationPerformanceItem> =
        emptyList()
)