package com.example.medication_demo.repository

import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.model.AppointmentCloudModel
import com.example.medication_demo.model.AppointmentUi
import com.example.medication_demo.model.toAppointmentUi
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class AppointmentCloudRepository {

    private val supabase =
        SupabaseClientProvider.client

    suspend fun getAppointments(): List<AppointmentUi> {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return emptyList()

        return try {
            supabase
                .from("appointments")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<AppointmentCloudModel>()
                .map { cloudAppointment ->
                    cloudAppointment.toAppointmentUi()
                }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun upsertAppointment(
        appointment: AppointmentUi
    ): Boolean {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return false

        return try {
            supabase
                .from("appointments")
                .upsert(
                    AppointmentCloudModel(
                        userId = userId,
                        id = appointment.id,
                        doctor = appointment.doctor,
                        appointmentName =
                            appointment.appointmentName,
                        date = appointment.date,
                        time = appointment.time,
                        location = appointment.location,
                        purpose = appointment.purpose,
                        notes = appointment.notes,
                        status = appointment.status,
                        reminderMinutesBefore =
                            appointment.reminderMinutesBefore,
                        isGoing = appointment.isGoing
                    )
                )

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun deleteAppointment(
        appointmentId: Int
    ): Boolean {
        val userId = supabase.auth.currentUserOrNull()
            ?.id ?: return false

        return try {
            supabase
                .from("appointments")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("id", appointmentId)
                    }
                }

            true
        } catch (_: Exception) {
            false
        }
    }
}