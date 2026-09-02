package com.example.medication_demo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MedicineInsertModel(
    @SerialName("user_id")
    val userId: String,

    val id: Int,
    val name: String,
    val quantity: String,

    @SerialName("dosage_amount")
    val dosageAmount: String,

    @SerialName("dosage_type")
    val dosageType: String,

    @SerialName("refill_reminder_enabled")
    val refillReminderEnabled: Boolean,

    @SerialName("refill_quantity")
    val refillQuantity: String,

    val frequency: String,

    @SerialName("reminder_times")
    val reminderTimes: List<ReminderTimeUi>,

    @SerialName("start_date")
    val startDate: String,

    val notes: String,

    @SerialName("reminder_enabled")
    val reminderEnabled: Boolean,

    @SerialName("preset_image_res")
    val presetImageRes: Int? = null,

    @SerialName("gallery_image_uri")
    val galleryImageUri: String? = null
)
