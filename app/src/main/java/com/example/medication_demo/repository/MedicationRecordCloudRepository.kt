package com.example.medication_demo.repository

import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.model.MedicationMissedRecordCloudModel
import com.example.medication_demo.model.MedicationTakenRecordCloudModel
import com.example.medication_demo.model.RescheduledDoseCloudModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class MedicationRecordCloudRepository {

    private val supabase =
        SupabaseClientProvider.client

    suspend fun getTakenRecords():
            List<MedicationTakenRecordCloudModel> {
        return getRecords(
            table = "medication_taken_records"
        )
    }

    suspend fun getMissedRecords():
            List<MedicationMissedRecordCloudModel> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from("medication_missed_records")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<MedicationMissedRecordCloudModel>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getRescheduledDoses():
            List<RescheduledDoseCloudModel> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from("medication_rescheduled_doses")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<RescheduledDoseCloudModel>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upsertTaken(
        record: MedicationTakenRecordCloudModel
    ): Boolean {
        return try {
            supabase
                .from("medication_taken_records")
                .upsert(record)

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun upsertMissed(
        record: MedicationMissedRecordCloudModel
    ): Boolean {
        return try {
            supabase
                .from("medication_missed_records")
                .upsert(record)

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun upsertRescheduled(
        dose: RescheduledDoseCloudModel
    ): Boolean {
        return try {
            supabase
                .from("medication_rescheduled_doses")
                .upsert(dose)

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun deleteMissed(
        medicineId: Int,
        date: String,
        doseIndex: Int
    ): Boolean {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return false

        return try {
            supabase
                .from("medication_missed_records")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("medicine_id", medicineId)
                        eq("dose_date", date)
                        eq("dose_index", doseIndex)
                    }
                }

            true
        } catch (_: Exception) {
            false
        }
    }



    private suspend fun getRecords(
        table: String
    ): List<MedicationTakenRecordCloudModel> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from(table)
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<MedicationTakenRecordCloudModel>()
        } catch (_: Exception) {
            emptyList()
        }
    }
}