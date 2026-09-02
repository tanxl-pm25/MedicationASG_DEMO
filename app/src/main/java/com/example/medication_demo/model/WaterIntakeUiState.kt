package com.example.medication_demo.model

import com.example.medication_demo.utils.getMalaysiaDate
import java.time.LocalDate

data class WaterIntakeUiState(
    val glasses: Int = 0,
    val dailyGoal: Int = 0,
    val selectedDate: LocalDate = getMalaysiaDate()
) {

    val progress: Float
        get() =
            if (dailyGoal > 0) {
                glasses.toFloat() / dailyGoal.toFloat()
            } else {
                0f
            }

    val message: String
        get() =when {
            dailyGoal == 0 -> "Set a daily goal to start tracking."
            glasses >= dailyGoal -> "Goal completed!"
            else -> "Keep drinking!"
        }
}