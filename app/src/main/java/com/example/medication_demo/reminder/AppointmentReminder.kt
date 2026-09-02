package com.example.medication_demo.reminder

import com.example.medication_demo.model.AppointmentUi
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medication_demo.R

object AppointmentReminder {
    private const val CHANNEL_ID =
        "appointment_reminders"

    private const val CHANNEL_NAME =
        "Appointment Reminders"

    private const val NOTIFICATION_ID_BASE = 5000

    fun show(
        context: Context,
        appointmentId: Int,
        doctor: String,
        appointmentName: String,
        date: String,
        time: String
    ) {
        createNotificationChannel(context)

        val confirmIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_CONFIRM_ATTENDANCE

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointmentId
            )
        }

        val confirmPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointmentId * 10 + 1,
                confirmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val unableIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_UNABLE_TO_ATTEND

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointmentId
            )
        }

        val unablePendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointmentId * 10 + 2,
                unableIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle("Appointment Reminder")
                .setContentText(
                    "$appointmentName with $doctor"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "$appointmentName with $doctor\n" +
                                    "$date at $time"
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "I Will Attend",
                    confirmPendingIntent
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Unable to Attend",
                    unablePendingIntent
                )
                .build()

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(
                NOTIFICATION_ID_BASE + appointmentId,
                notification
            )
    }

    fun dismiss(
        context: Context,
        appointmentId: Int
    ) {
        NotificationManagerCompat
            .from(context)
            .cancel(5000 + appointmentId)
    }

    fun showCompletionCheck(
        context: Context,
        appointment: AppointmentUi
    ) {
        createNotificationChannel(context)

        val completedIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_MARK_COMPLETED

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointment.id
            )
        }

        val completedPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointment.id * 10 + 3,
                completedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val missedIntent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            action =
                AppointmentNotificationReceiver
                    .ACTION_MARK_MISSED

            putExtra(
                AppointmentNotificationReceiver
                    .EXTRA_APPOINTMENT_ID,
                appointment.id
            )
        }

        val missedPendingIntent =
            PendingIntent.getBroadcast(
                context,
                appointment.id * 10 + 4,
                missedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle("Appointment Follow-up")
                .setContentText(
                    "Did you attend your appointment?"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "Did you attend ${appointment.appointmentName} " +
                                    "with ${appointment.doctor}?"
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Mark as Completed",
                    completedPendingIntent
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Mark as Missed",
                    missedPendingIntent
                )
                .build()

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(
                NOTIFICATION_ID_BASE + appointment.id,
                notification
            )
    }

    fun showTestNotification(
        context: Context,
        appointment: AppointmentUi
    ) {
        if (appointment.isGoing) {
            showCompletionCheck(
                context = context,
                appointment = appointment
            )
        } else {
            show(
                context = context,
                appointmentId = appointment.id,
                doctor = appointment.doctor,
                appointmentName = appointment.appointmentName,
                date = appointment.date,
                time = appointment.time
            )
        }
    }

    private fun createNotificationChannel(
        context: Context
    ) {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Reminders for medical appointments"

                enableVibration(true)
            }

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}