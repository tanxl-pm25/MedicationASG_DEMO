package com.example.medication_demo.model

import java.time.YearMonth

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
        YearMonth.of(2026, 7),

    val missedMedicines: List<MissedMedicine> =
        listOf(

            MissedMedicine(
                day = "3",
                month = "Jul 2026",
                weekday = "Thu",
                medicineName = "Vitamin D3 1000IU",
                scheduledTime = "8:00 AM",
                dosage = "1000 IU"
            ),

            MissedMedicine(
                day = "12",
                month = "Jul 2026",
                weekday = "Sat",
                medicineName = "Metformin 500mg",
                scheduledTime = "8:00 AM",
                dosage = "500 mg"
            ),

            MissedMedicine(
                day = "18",
                month = "Jul 2026",
                weekday = "Fri",
                medicineName = "Omega-3 1000mg",
                scheduledTime = "1:00 PM",
                dosage = "1000 mg"
            ),

            MissedMedicine(
                day = "21",
                month = "Jul 2026",
                weekday = "Mon",
                medicineName = "Metformin 500mg",
                scheduledTime = "8:00 AM",
                dosage = "500 mg"
            ),

            MissedMedicine(
                day = "25",
                month = "Jul 2026",
                weekday = "Fri",
                medicineName = "Omega-3 1000mg",
                scheduledTime = "1:00 PM",
                dosage = "1000 mg"
            ),

            MissedMedicine(
                day = "29",
                month = "Jul 2026",
                weekday = "Tue",
                medicineName = "Metformin 500mg",
                scheduledTime = "8:00 AM",
                dosage = "500 mg"
            )
        )
) {

    val missedCount: Int
        get() = missedMedicines.size
}