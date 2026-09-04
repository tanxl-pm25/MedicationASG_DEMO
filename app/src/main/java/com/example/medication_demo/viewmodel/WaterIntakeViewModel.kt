package com.example.medication_demo.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.medication_demo.model.WaterGoalCloudModel
import com.example.medication_demo.model.WaterProfileCloudModel
import com.example.medication_demo.model.WaterRecordCloudModel
import com.example.medication_demo.repository.WaterRepository
import kotlinx.coroutines.launch
import com.example.medication_demo.storage.CurrentUserStorage
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
        const val KEY_WATER_STARTED_DATE = "water_started_date"
        const val KEY_WATER_GOAL_HISTORY = "water_goal_history"
    }

    private var currentUserId = "guest"
    private lateinit var preferences: android.content.SharedPreferences
    private lateinit var waterStartedDate: LocalDate
    private val waterRecords = mutableMapOf<LocalDate, Int>()
    private val waterGoalHistory = mutableMapOf<LocalDate, Int>()
    private val _uiState = MutableStateFlow(WaterIntakeUiState())
    private val waterRepository = WaterRepository()
    val uiState: StateFlow<WaterIntakeUiState> = _uiState.asStateFlow()

    fun initialize(
        context: Context
    ) {
        val userId =
            CurrentUserStorage.getUserId(
                context
            ) ?: "guest"

        switchUser(
            context = context,
            userId = userId
        )
    }

    fun switchUser(
        context: Context,
        userId: String
    ) {
        if (
            ::preferences.isInitialized &&
            currentUserId == userId
        ) {
            return
        }

        currentUserId = userId

        preferences = context.getSharedPreferences(
            "${PREFERENCES_NAME}_$userId",
            Context.MODE_PRIVATE
        )

        waterRecords.clear()
        waterGoalHistory.clear()

        waterRecords.putAll(
            loadWaterRecords()
        )

        waterGoalHistory.putAll(
            loadWaterGoalHistory()
        )

        val savedStartedDate =
            preferences.getString(
                KEY_WATER_STARTED_DATE,
                null
            )

        waterStartedDate =
            savedStartedDate
                ?.let { dateText ->
                    try {
                        LocalDate.parse(dateText)
                    } catch (_: Exception) {
                        null
                    }
                }
                ?: getMalaysiaDate().also { today ->
                    preferences.edit()
                        .putString(
                            KEY_WATER_STARTED_DATE,
                            today.toString()
                        )
                        .apply()
                }

        if (waterGoalHistory.isEmpty()) {
            val oldGoal = preferences.getInt(
                KEY_DAILY_GOAL,
                0
            )

            if (oldGoal > 0) {
                waterGoalHistory[waterStartedDate] =
                    oldGoal
                saveWaterGoalHistory()
            }
        }

        val selectedDate = getMalaysiaDate()

        _uiState.value = WaterIntakeUiState(
            glasses = waterRecords[selectedDate] ?: 0,
            dailyGoal = getGoalForDate(selectedDate),
            selectedDate = selectedDate
        )

        syncWaterWithCloud()
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

    fun isSelectedDateEditable(): Boolean {
        val selectedDate =
            _uiState.value.selectedDate

        return !selectedDate.isBefore(
            waterStartedDate
        ) &&
                !selectedDate.isAfter(
                    getMalaysiaDate()
                )
    }

    fun isSelectedDateBeforeWaterStarted():
            Boolean {
        return _uiState.value.selectedDate
            .isBefore(waterStartedDate)
    }

    fun addGlass() {
        if (!isSelectedDateEditable()) {
            return
        }

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
        if (!isSelectedDateEditable()) {
            return
        }

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

        if (selectedDate.isAfter(getMalaysiaDate())) {
            return
        }

        _uiState.value = _uiState.value.copy(
            selectedDate = selectedDate,
            glasses = waterRecords[selectedDate] ?: 0,
            dailyGoal = getGoalForDate(
                selectedDate
            )
        )
    }

    fun updateGoal(
        newGoal: Int
    ) {
        if (newGoal <= 0) {
            return
        }

        val effectiveDate =
            getMalaysiaDate()

        waterGoalHistory[effectiveDate] =
            newGoal

        saveWaterGoalHistory()

        uploadGoal(
            date = effectiveDate,
            goal = newGoal
        )

        // Keep this for old-version migration only.
        preferences.edit()
            .putInt(
                KEY_DAILY_GOAL,
                newGoal
            )
            .apply()

        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            dailyGoal = getGoalForDate(
                currentState.selectedDate
            )
        )
    }

    private fun getGoalForDate(
        date: LocalDate
    ): Int {
        return waterGoalHistory
            .filterKeys { goalDate ->
                !goalDate.isAfter(date)
            }
            .maxByOrNull { entry ->
                entry.key
            }
            ?.value
            ?: 0
    }

    private fun saveWaterGoalHistory() {
        val goalsJson = JSONObject()

        waterGoalHistory.forEach {
                (goalDate, goal) ->
            goalsJson.put(
                goalDate.toString(),
                goal
            )
        }

        preferences.edit()
            .putString(
                KEY_WATER_GOAL_HISTORY,
                goalsJson.toString()
            )
            .apply()
    }

    private fun loadWaterGoalHistory():
            Map<LocalDate, Int> {
        val savedJson = preferences.getString(
            KEY_WATER_GOAL_HISTORY,
            null
        ) ?: return emptyMap()

        return try {
            val goalsJson = JSONObject(savedJson)
            val goals = mutableMapOf<LocalDate, Int>()

            goalsJson.keys().forEach { dateText ->
                goals[LocalDate.parse(dateText)] =
                    goalsJson.getInt(dateText)
            }

            goals
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun saveGlassRecord(
        date: LocalDate,
        glasses: Int
    ) {
        waterRecords[date] = glasses

        saveWaterRecords()

        uploadRecord(
            date = date,
            glasses = glasses
        )
    }

    private fun saveWaterRecords() {
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

    private fun uploadProfile() {
        if (currentUserId == "guest") return

        viewModelScope.launch {
            waterRepository.upsertProfile(
                WaterProfileCloudModel(
                    userId = currentUserId,
                    waterStartedDate =
                        waterStartedDate.toString()
                )
            )
        }
    }

    private fun uploadGoal(
        date: LocalDate,
        goal: Int
    ) {
        if (currentUserId == "guest") return

        viewModelScope.launch {
            waterRepository.upsertGoal(
                WaterGoalCloudModel(
                    userId = currentUserId,
                    effectiveDate = date.toString(),
                    dailyGoal = goal
                )
            )
        }
    }

    private fun uploadRecord(
        date: LocalDate,
        glasses: Int
    ) {
        if (currentUserId == "guest") return

        viewModelScope.launch {
            waterRepository.upsertRecord(
                WaterRecordCloudModel(
                    userId = currentUserId,
                    recordDate = date.toString(),
                    glasses = glasses
                )
            )
        }
    }

    private fun syncWaterWithCloud() {
        if (currentUserId == "guest") return

        viewModelScope.launch {
            val cloudProfile = waterRepository.getProfile()
            val cloudGoals = waterRepository.getGoals()
            val cloudRecords = waterRepository.getRecords()

            cloudProfile?.waterStartedDate?.let { dateText ->
                try {
                    val cloudStartedDate =
                        LocalDate.parse(dateText)

                    if (
                        cloudStartedDate.isBefore(
                            waterStartedDate
                        )
                    ) {
                        waterStartedDate = cloudStartedDate

                        preferences.edit()
                            .putString(
                                KEY_WATER_STARTED_DATE,
                                waterStartedDate.toString()
                            )
                            .apply()
                    }
                } catch (_: Exception) {
                }
            }

            cloudGoals.forEach { goal ->
                try {
                    waterGoalHistory[
                        LocalDate.parse(goal.effectiveDate)
                    ] = goal.dailyGoal
                } catch (_: Exception) {
                }
            }

            cloudRecords.forEach { record ->
                try {
                    waterRecords[
                        LocalDate.parse(record.recordDate)
                    ] = record.glasses
                } catch (_: Exception) {
                }
            }

            saveWaterGoalHistory()
            saveWaterRecords()

            val selectedDate =
                _uiState.value.selectedDate

            _uiState.value = _uiState.value.copy(
                glasses = waterRecords[selectedDate] ?: 0,
                dailyGoal = getGoalForDate(selectedDate)
            )

            uploadProfile()

            waterGoalHistory.forEach { (date, goal) ->
                uploadGoal(
                    date = date,
                    goal = goal
                )
            }

            waterRecords.forEach { (date, glasses) ->
                uploadRecord(
                    date = date,
                    glasses = glasses
                )
            }
        }
    }

}