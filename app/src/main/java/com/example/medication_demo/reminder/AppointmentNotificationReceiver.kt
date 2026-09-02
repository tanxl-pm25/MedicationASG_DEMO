package com.example.medication_demo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medication_demo.repository.AppointmentRepository

class AppointmentNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val appointmentId = intent.getIntExtra(
            EXTRA_APPOINTMENT_ID,
            -1
        )

        if (appointmentId == -1) {
            return
        }

        when (intent.action) {
            ACTION_CONFIRM_ATTENDANCE -> {
                AppointmentRepository.markGoing(
                    appointmentId
                )

                AppointmentReminder.dismiss(
                    context,
                    appointmentId
                )
            }

            ACTION_SHOW_FOLLOW_UP -> {
                val appointment =
                    AppointmentRepository.getAppointmentById(
                        appointmentId
                    )

                if (
                    appointment != null &&
                    appointment.isGoing &&
                    appointment.status ==
                    com.example.medication_demo.model
                        .AppointmentStatus.UPCOMING
                ) {
                    AppointmentReminder.showCompletionCheck(
                        context = context,
                        appointment = appointment
                    )
                }
            }

            ACTION_SHOW_REMINDER -> {
                AppointmentReminder.show(
                    context = context,
                    appointmentId = appointmentId,
                    doctor = intent.getStringExtra(
                        EXTRA_DOCTOR
                    ).orEmpty(),
                    appointmentName = intent.getStringExtra(
                        EXTRA_APPOINTMENT_NAME
                    ).orEmpty(),
                    date = intent.getStringExtra(
                        EXTRA_DATE
                    ).orEmpty(),
                    time = intent.getStringExtra(
                        EXTRA_TIME
                    ).orEmpty()
                )
            }



            ACTION_UNABLE_TO_ATTEND -> {
                AppointmentRepository.cancelAppointment(
                    appointmentId
                )

                AppointmentReminder.dismiss(
                    context,
                    appointmentId
                )
            }

            ACTION_MARK_COMPLETED -> {
                AppointmentRepository.markAsCompleted(
                    appointmentId
                )

                AppointmentReminder.dismiss(
                    context,
                    appointmentId
                )
            }

            ACTION_MARK_MISSED -> {
                AppointmentRepository.markAsMissed(
                    appointmentId
                )

                AppointmentReminder.dismiss(
                    context,
                    appointmentId
                )
            }
        }
    }

    companion object {
        const val ACTION_SHOW_FOLLOW_UP = "com.example.medication_demo.action.SHOW_APPOINTMENT_FOLLOW_UP"
        const val EXTRA_APPOINTMENT_ID = "appointment_id"

        const val EXTRA_DOCTOR = "appointment_doctor"

        const val EXTRA_APPOINTMENT_NAME = "appointment_name"
        const val EXTRA_DATE = "appointment_date"
        const val EXTRA_TIME = "appointment_time"
        const val ACTION_SHOW_REMINDER = "com.example.medication_demo.action.SHOW_APPOINTMENT_REMINDER"
        const val ACTION_CONFIRM_ATTENDANCE = "com.example.medication_demo.action.CONFIRM_ATTENDANCE"
        const val ACTION_UNABLE_TO_ATTEND = "com.example.medication_demo.action.UNABLE_TO_ATTEND"
        const val ACTION_MARK_COMPLETED = "com.example.medication_demo.action.MARK_COMPLETED"
        const val ACTION_MARK_MISSED = "com.example.medication_demo.action.MARK_MISSED"
    }
}