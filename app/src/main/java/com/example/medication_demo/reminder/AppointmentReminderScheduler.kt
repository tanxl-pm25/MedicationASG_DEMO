package com.example.medication_demo.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medication_demo.model.AppointmentUi
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object AppointmentReminderScheduler {

    private val appointmentFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy hh:mm a",
            Locale.ENGLISH
        )

    fun schedule(
        context: Context,
        appointment: AppointmentUi
    ) {
        val minutesBefore =
            appointment.reminderMinutesBefore
                ?: return

        val appointmentDateTime = try {
            LocalDateTime.parse(
                "${appointment.date} ${appointment.time}",
                appointmentFormatter
            )
        } catch (_: Exception) {
            return
        }

        val triggerMillis = appointmentDateTime
            .minusMinutes(minutesBefore.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (triggerMillis <= System.currentTimeMillis()) {
            return
        }

        val reminderIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_SHOW_REMINDER

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointment.id
            )

            putExtra(
                AppointmentNotificationReceiver.EXTRA_DOCTOR,
                appointment.doctor
            )

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_NAME,
                appointment.appointmentName
            )

            putExtra(
                AppointmentNotificationReceiver.EXTRA_DATE,
                appointment.date
            )

            putExtra(
                AppointmentNotificationReceiver.EXTRA_TIME,
                appointment.time
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointment.id,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S &&
            alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }

        scheduleFollowUp(
            context = context,
            appointment = appointment,
            appointmentDateTime = appointmentDateTime
        )

    }

    private fun scheduleFollowUp(
        context: Context,
        appointment: AppointmentUi,
        appointmentDateTime: LocalDateTime
    ) {

        val followUpMillis = appointmentDateTime
            .plusHours(1)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (followUpMillis <= System.currentTimeMillis()) {
            return
        }

        val followUpIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_SHOW_FOLLOW_UP

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointment.id
            )
        }

        val followUpPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointment.id + 100000,
                followUpIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S &&
            alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                followUpMillis,
                followUpPendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                followUpMillis,
                followUpPendingIntent
            )
        }
    }

    fun cancel(
        context: Context,
        appointmentId: Int
    ) {
        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val reminderIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_SHOW_REMINDER
        }

        val reminderPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointmentId,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(reminderPendingIntent)

        val followUpIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_SHOW_FOLLOW_UP
        }

        val followUpPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointmentId + 100000,
                followUpIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(followUpPendingIntent)
    }
}