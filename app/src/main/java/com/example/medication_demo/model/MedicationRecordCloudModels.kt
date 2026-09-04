package com.example.medication_demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class MedicationTakenRecordCloudModel(
    @SerialName("user_id")
    val userId: String,

    @SerialName("medicine_id")
    val medicineId: Int,

    @SerialName("dose_date")
    val date: String,

    @SerialName("dose_index")
    val doseIndex: Int,

    @SerialName("reminder_time")
    val reminderTime: String,

    @SerialName("taken_time")
    val takenTime: String,

    @SerialName("dosage_amount")
    val dosageAmount: String,

    @SerialName("dosage_type")
    val dosageType: String
)

@Serializable
data class MedicationMissedRecordCloudModel(
    @SerialName("user_id")
    val userId: String,
    @SerialName("medicine_id")
    val medicineId: Int,
    @SerialName("dose_date")
    val date: String,
    @SerialName("dose_index")
    val doseIndex: Int,
    @SerialName("reminder_time")
    val reminderTime: String,
    @SerialName("dosage_amount")
    val dosageAmount: String,
    @SerialName("dosage_type")
    val dosageType: String
)

@Serializable
data class RescheduledDoseCloudModel(
    @SerialName("user_id")
    val userId: String,
    @SerialName("medicine_id")
    val medicineId: Int,
    @SerialName("dose_date")
    val date: String,
    @SerialName("dose_index")
    val doseIndex: Int,
    @SerialName("original_time")
    val originalTime: String,
    @SerialName("new_time")
    val newTime: String
)