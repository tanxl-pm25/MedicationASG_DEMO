package com.example.medication_demo.model

import java.time.YearMonth
import java.time.ZoneId

data class MissedMedicine(
    val day: String,
    val month: String,
    val weekday: String,
    val medicineName: String,
    val scheduledTime: String,
    val dosage: String
)

data class MissedMedicationUiState(
    val selectedMonth: YearMonth =
        YearMonth.now(
            ZoneId.of("Asia/Kuala_Lumpur")
        ),

    val missedMedicines: List<MissedMedicine> =
        emptyList()
) {
    val missedCount: Int
        get() = missedMedicines.size
}