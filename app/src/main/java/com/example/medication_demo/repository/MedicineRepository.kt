package com.example.medication_demo.repository

import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MedicineCloudModel
import com.example.medication_demo.model.toMedicine
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import com.example.medication_demo.model.MedicineInsertModel

class MedicineRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getMedicines(): List<Medicine> {

        val userId =
            supabase.auth.currentUserOrNull()
                ?.id
                ?: return emptyList()

        return supabase
            .from("medicines")
            .select {
                filter {
                    eq(
                        "user_id",
                        userId
                    )
                }
            }
            .decodeList<MedicineCloudModel>()
            .map { cloudMedicine ->
                cloudMedicine.toMedicine()
            }
    }
    suspend fun addMedicine(
        medicine: Medicine
    ): Boolean {
        val userId =
            supabase.auth.currentUserOrNull()
                ?.id
                ?: return false
        val insertModel =
            MedicineInsertModel(
                userId = userId,
                id = medicine.id,
                name = medicine.name,
                quantity = medicine.quantity,
                dosageAmount = medicine.dosageAmount,
                dosageType = medicine.dosageType,
                refillReminderEnabled = medicine.refillReminderEnabled,
                refillQuantity = medicine.refillQuantity,
                frequency = medicine.frequency,
                reminderTimes = medicine.reminderTimes,
                startDate = medicine.startDate,
                notes = medicine.notes,
                reminderEnabled = medicine.reminderEnabled,
                presetImageRes = medicine.presetImageRes,
                galleryImageUri = medicine.galleryImageUri,
                repeatReminderEnabled = medicine.repeatReminderEnabled,
                repeatIntervalMinutes = medicine.repeatIntervalMinutes,
                repeatCount = medicine.repeatCount
            )
        return try {
            supabase
                .from("medicines")
                .insert(insertModel)
            true
        } catch (e: Exception) {
            android.util.Log.e("MedicineRepository", "addMedicine failed: ${e.message}", e)
        false
    }
    }
    suspend fun updateMedicine(
        medicine: Medicine
    ): Boolean {
        val userId =
            supabase.auth.currentUserOrNull()
                ?.id
                ?: return false
        return try {
            supabase
                .from("medicines")
                .update(
                    {
                        set("name", medicine.name)
                        set("quantity", medicine.quantity)
                        set("dosage_amount", medicine.dosageAmount)
                        set("dosage_type", medicine.dosageType)
                        set(
                            "refill_reminder_enabled",
                            medicine.refillReminderEnabled
                        )
                        set(
                            "refill_quantity",
                            medicine.refillQuantity
                        )
                        set("frequency", medicine.frequency)
                        set(
                            "reminder_times",
                            medicine.reminderTimes
                        )
                        set("start_date", medicine.startDate)
                        set("notes", medicine.notes)
                        set(
                            "reminder_enabled",
                            medicine.reminderEnabled
                        )
                        set(
                            "preset_image_res",
                            medicine.presetImageRes
                        )
                        set(
                            "gallery_image_uri",
                            medicine.galleryImageUri
                        )
                        set(
                            "repeat_reminder_enabled",
                            medicine.repeatReminderEnabled
                        )
                        set(
                            "repeat_interval_minutes",
                            medicine.repeatIntervalMinutes
                        )
                        set(
                            "repeat_count",
                            medicine.repeatCount
                        )
                    }
                ) {
                    filter {
                        eq("user_id", userId)
                        eq("id", medicine.id)
                    }
                }
            true
        } catch (_: Exception) {
            false
        }
    }
    suspend fun deleteMedicine(
        medicineId: Int
    ): Boolean {
        val userId =
            supabase.auth.currentUserOrNull()
                ?.id
                ?: return false
        return try {
            supabase
                .from("medicines")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("id", medicineId)
                    }
                }
            true
        } catch (_: Exception) {
            false
        }
    }
}