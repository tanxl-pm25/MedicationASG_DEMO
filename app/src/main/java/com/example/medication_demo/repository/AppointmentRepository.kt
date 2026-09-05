package com.example.medication_demo.repository

import com.example.medication_demo.reminder.AppointmentReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.medication_demo.storage.CurrentUserStorage
import android.content.Context
import com.example.medication_demo.model.AppointmentStatus
import com.example.medication_demo.model.AppointmentUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AppointmentRepository {

    private const val PREFERENCES_NAME = "appointment_preferences"
    private const val KEY_APPOINTMENTS = "saved_appointments"

    private val json = Json { ignoreUnknownKeys = true }
    private val cloudRepository = AppointmentCloudRepository()
    private val cloudScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var preferences: android.content.SharedPreferences
    private var currentUserId: String? = null

    private val _appointments =
        MutableStateFlow<List<AppointmentUi>>(emptyList())

    val appointments: StateFlow<List<AppointmentUi>> =
        _appointments.asStateFlow()

    fun initialize(context: Context) {
        val userId = CurrentUserStorage.getUserId(context)
            ?: "guest"

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

        _appointments.value = loadAppointments()
        syncAppointmentsFromCloud(context)
    }

    fun getAppointmentById(
        appointmentId: Int
    ): AppointmentUi? {
        return _appointments.value.firstOrNull {
            it.id == appointmentId
        }
    }

    fun getNextId(): Int {
        return (_appointments.value.maxOfOrNull {
            it.id
        } ?: 0) + 1
    }

    fun addAppointment(
        appointment: AppointmentUi
    ) {
        saveAppointments(
            _appointments.value + appointment
        )

        syncUpsert(appointment)
    }

    fun updateAppointment(
        updatedAppointment: AppointmentUi
    ) {
        saveAppointments(
            _appointments.value.map { appointment ->
                if (appointment.id == updatedAppointment.id) {
                    updatedAppointment
                } else {
                    appointment
                }
            }
        )

        syncUpsert(updatedAppointment)
    }

    fun deleteAppointment(
        appointmentId: Int
    ) {
        saveAppointments(
            _appointments.value.filter {
                it.id != appointmentId
            }
        )

        cloudScope.launch {
            cloudRepository.deleteAppointment(
                appointmentId
            )
        }
    }

    fun markGoing(
        appointmentId: Int
    ) {
        updateAppointmentStatus(
            appointmentId = appointmentId,
            status = AppointmentStatus.UPCOMING,
            isGoing = true
        )
    }

    fun cancelAppointment(
        appointmentId: Int
    ) {
        updateAppointmentStatus(
            appointmentId = appointmentId,
            status = AppointmentStatus.CANCELLED,
            isGoing = false
        )
    }

    fun markAsCompleted(
        appointmentId: Int
    ) {
        updateAppointmentStatus(
            appointmentId = appointmentId,
            status = AppointmentStatus.COMPLETED,
            isGoing = true
        )
    }

    fun markAsMissed(
        appointmentId: Int
    ) {
        updateAppointmentStatus(
            appointmentId = appointmentId,
            status = AppointmentStatus.MISSED,
            isGoing = false
        )
    }

    fun markAsRescheduled(
        appointmentId: Int
    ) {
        updateAppointmentStatus(
            appointmentId = appointmentId,
            status = AppointmentStatus.RESCHEDULED,
            isGoing = false
        )
    }

    private fun updateAppointmentStatus(
        appointmentId: Int,
        status: AppointmentStatus,
        isGoing: Boolean
    ) {
        saveAppointments(
            _appointments.value.map { appointment ->
                if (appointment.id == appointmentId) {
                    appointment.copy(
                        status = status,
                        isGoing = isGoing
                    )
                } else {
                    appointment
                }
            }
        )
    }

    private fun loadAppointments(): List<AppointmentUi> {
        val savedJson = preferences.getString(
            KEY_APPOINTMENTS,
            null
        ) ?: return emptyList()

        return try {
            json.decodeFromString<List<AppointmentUi>>(
                savedJson
            )
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveAppointments(
        updatedAppointments: List<AppointmentUi>
    ) {
        _appointments.value = updatedAppointments

        preferences.edit()
            .putString(
                KEY_APPOINTMENTS,
                json.encodeToString(updatedAppointments)
            )
            .apply()
    }

    private fun syncUpsert(
        appointment: AppointmentUi
    ) {
        cloudScope.launch {
            cloudRepository.upsertAppointment(
                appointment
            )
        }
    }

    private fun syncAppointmentsFromCloud(
        context: Context
    ) {
        if (currentUserId == null || currentUserId == "guest") {
            return
        }
        cloudScope.launch {
            _appointments.value.forEach { appointment ->
                cloudRepository.upsertAppointment(
                    appointment
                )
            }

            val cloudAppointments =
                cloudRepository.getAppointments()

            if (cloudAppointments.isEmpty()) {
                return@launch
            }

            _appointments.value = cloudAppointments
            saveAppointments(cloudAppointments)

            cloudAppointments
                .filter { appointment ->
                    appointment.status ==
                            AppointmentStatus.UPCOMING
                }
                .forEach { appointment ->
                    AppointmentReminderScheduler.schedule(
                        context = context,
                        appointment = appointment
                    )
                }
        }
    }
}