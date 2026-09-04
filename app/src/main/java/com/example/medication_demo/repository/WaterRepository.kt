package com.example.medication_demo.repository

import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.model.WaterGoalCloudModel
import com.example.medication_demo.model.WaterProfileCloudModel
import com.example.medication_demo.model.WaterRecordCloudModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class WaterRepository {

    private val supabase =
        SupabaseClientProvider.client

    suspend fun getProfile(): WaterProfileCloudModel? {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return null

        return try {
            supabase
                .from("water_profiles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<WaterProfileCloudModel>()
                .firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getGoals(): List<WaterGoalCloudModel> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from("water_goals")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<WaterGoalCloudModel>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getRecords(): List<WaterRecordCloudModel> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from("water_records")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<WaterRecordCloudModel>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upsertProfile(
        profile: WaterProfileCloudModel
    ): Boolean {
        return try {
            supabase
                .from("water_profiles")
                .upsert(profile)

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun upsertGoal(
        goal: WaterGoalCloudModel
    ): Boolean {
        return try {
            supabase
                .from("water_goals")
                .upsert(goal)

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun upsertRecord(
        record: WaterRecordCloudModel
    ): Boolean {
        return try {
            supabase
                .from("water_records")
                .upsert(record)

            true
        } catch (_: Exception) {
            false
        }
    }
}