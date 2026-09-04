package com.example.medication_demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WaterProfileCloudModel(
    @SerialName("user_id")
    val userId: String,

    @SerialName("water_started_date")
    val waterStartedDate: String
)

@Serializable
data class WaterGoalCloudModel(
    @SerialName("user_id")
    val userId: String,

    @SerialName("effective_date")
    val effectiveDate: String,

    @SerialName("daily_goal")
    val dailyGoal: Int
)

@Serializable
data class WaterRecordCloudModel(
    @SerialName("user_id")
    val userId: String,

    @SerialName("record_date")
    val recordDate: String,

    val glasses: Int
)