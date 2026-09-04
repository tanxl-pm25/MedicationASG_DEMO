package com.example.medication_demo.reminder

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
import com.example.medication_demo.MainActivity
import com.example.medication_demo.R

object MedicationNotification {

    private const val CHANNEL_ID = "medication_reminders"

    private const val NOTIFICATION_ID_BASE = 7000

    const val EXTRA_ACTION = "medication_notification_action"
    const val EXTRA_MEDICINE_ID = "medication_id"
    const val EXTRA_DOSE_INDEX = "medication_dose_index"
    const val EXTRA_ORIGINAL_TIME = "medication_original_time"
    const val ACTION_TAKEN = "medication_action_taken"
    const val ACTION_RESCHEDULE = "medication_action_reschedule"
    const val ACTION_OPEN_REMINDER = "medication_action_open_reminder"

    fun show(
        context: Context,
        medicineId: Int,
        doseIndex: Int,
        originalTime: String,
        medicineName: String,
        dosage: String
    ) {
        createChannel(context)

        val openReminderPendingIntent = actionIntent(
            context = context,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = originalTime,
            action = ACTION_OPEN_REMINDER
        )

        val takenPendingIntent = actionIntent(
            context = context,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = originalTime,
            action = ACTION_TAKEN
        )

        val reschedulePendingIntent = actionIntent(
            context = context,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = originalTime,
            action = ACTION_RESCHEDULE
        )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle("Medication Reminder")
                .setContentText(
                    "$medicineName • $dosage"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "It is time to take $medicineName.\n" +
                                    dosage
                        )
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)
                .setContentIntent(openReminderPendingIntent)
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Taken",
                    takenPendingIntent
                )
                .addAction(
                    R.drawable.ic_launcher_foreground,
                    "Reschedule",
                    reschedulePendingIntent
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
                NOTIFICATION_ID_BASE + medicineId * 10 + doseIndex,
                notification
            )
    }

    private fun actionIntent(
        context: Context,
        medicineId: Int,
        doseIndex: Int,
        originalTime: String,
        action: String
    ): PendingIntent {
        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(EXTRA_ACTION, action)
            putExtra(EXTRA_MEDICINE_ID, medicineId)
            putExtra(EXTRA_DOSE_INDEX, doseIndex)
            putExtra(EXTRA_ORIGINAL_TIME, originalTime)
        }

        return PendingIntent.getActivity(
            context,
            medicineId * 100 + doseIndex +
                    when (action) {
                        ACTION_TAKEN -> 1
                        ACTION_RESCHEDULE -> 2
                        ACTION_OPEN_REMINDER -> 3
                        else -> 0
                    },
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel(
        context: Context
    ) {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled medication"
            }

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}