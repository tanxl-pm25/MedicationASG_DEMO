package com.example.medication_demo.viewmodel

import com.example.medication_demo.utils.getMalaysiaDate
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.WaterIntakeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.time.LocalDate

class WaterIntakeViewModel : ViewModel() {

    private companion object {
        const val PREFERENCES_NAME = "water_intake_preferences"
        const val KEY_DAILY_GOAL = "daily_goal"
        const val KEY_WATER_RECORDS = "water_records"
    }

    private lateinit var preferences: android.content.SharedPreferences
    private val waterRecords = mutableMapOf<LocalDate, Int>()
    private val _uiState = MutableStateFlow(WaterIntakeUiState())

    val uiState: StateFlow<WaterIntakeUiState> = _uiState.asStateFlow()

    fun initialize(context: Context) {
        if (::preferences.isInitialized) {
            return
        }

        preferences = context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        waterRecords.putAll(loadWaterRecords())

        val selectedDate = getMalaysiaDate()

        val dailyGoal = preferences.getInt(
            KEY_DAILY_GOAL,
            0
        )

        _uiState.value = WaterIntakeUiState(
            glasses = waterRecords[selectedDate] ?: 0,
            dailyGoal = dailyGoal,
            selectedDate = selectedDate
        )
    }

    fun selectToday() {
        val today = getMalaysiaDate()

        selectDate(
            year = today.year,
            month = today.monthValue,
            day = today.dayOfMonth
        )
    }

    fun getTodayGlasses(): Int {
        return waterRecords[
            getMalaysiaDate()
        ] ?: 0
    }

    fun addGlass() {
        val currentState = _uiState.value

        if (
            currentState.dailyGoal > 0 &&
            currentState.glasses >= currentState.dailyGoal
        ) {
            return
        }

        val newGlasses = currentState.glasses + 1

        saveGlassRecord(
            date = currentState.selectedDate,
            glasses = newGlasses
        )

        _uiState.value = currentState.copy(
            glasses = newGlasses
        )
    }

    fun removeGlass() {
        val currentState = _uiState.value

        if (currentState.glasses <= 0) {
            return
        }

        val newGlasses = currentState.glasses - 1

        saveGlassRecord(
            date = currentState.selectedDate,
            glasses = newGlasses
        )

        _uiState.value = currentState.copy(
            glasses = newGlasses
        )
    }

    fun selectDate(
        year: Int,
        month: Int,
        day: Int
    ) {
        val selectedDate = LocalDate.of(
            year,
            month,
            day
        )

        _uiState.value = _uiState.value.copy(
            selectedDate = selectedDate,
            glasses = waterRecords[selectedDate] ?: 0
        )
    }

    fun updateGoal(newGoal: Int) {
        if (newGoal <= 0) {
            return
        }

        preferences.edit()
            .putInt(KEY_DAILY_GOAL, newGoal)
            .apply()

        val currentState = _uiState.value
        val adjustedGlasses =
            currentState.glasses.coerceAtMost(newGoal)

        saveGlassRecord(
            date = currentState.selectedDate,
            glasses = adjustedGlasses
        )

        _uiState.value = currentState.copy(
            dailyGoal = newGoal,
            glasses = adjustedGlasses
        )
    }

    private fun saveGlassRecord(
        date: LocalDate,
        glasses: Int
    ) {
        waterRecords[date] = glasses

        val recordsJson = JSONObject()

        waterRecords.forEach { (recordDate, recordGlasses) ->
            recordsJson.put(
                recordDate.toString(),
                recordGlasses
            )
        }

        preferences.edit()
            .putString(
                KEY_WATER_RECORDS,
                recordsJson.toString()
            )
            .apply()
    }

    private fun loadWaterRecords(): Map<LocalDate, Int> {
        val savedJson = preferences.getString(
            KEY_WATER_RECORDS,
            null
        ) ?: return emptyMap()

        return try {
            val recordsJson = JSONObject(savedJson)
            val records = mutableMapOf<LocalDate, Int>()

            recordsJson.keys().forEach { dateText ->
                records[LocalDate.parse(dateText)] =
                    recordsJson.getInt(dateText)
            }

            records
        } catch (_: Exception) {
            emptyMap()
        }
    }
}