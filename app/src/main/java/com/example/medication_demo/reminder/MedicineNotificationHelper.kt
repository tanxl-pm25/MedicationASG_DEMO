package com.example.medication_demo.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medication_demo.MainActivity
import com.example.medication_demo.R

private const val MEDICINE_CHANNEL_ID =
    "medicine_reminder_channel"

fun createMedicineNotificationChannel(
    context: Context
) {
    val channel =
        NotificationChannel(
            MEDICINE_CHANNEL_ID,
            "Medicine Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description =
                "Notifications for scheduled medicine doses"

            enableVibration(true)
        }

    val manager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    manager.createNotificationChannel(channel)
}


fun showMedicineNotification(
    context: Context,
    medicineId: Int,
    medicineName: String,
    scheduledTime: String,
    repeatNumber: Int = 0
){
    val title =
        if (repeatNumber == 0) {
            "Time to take your medicine"
        } else {
            "Medicine reminder"
        }

    val message =
        if (repeatNumber == 0) {
            "$medicineName was scheduled for $scheduledTime."
        } else {
            "You haven't marked $medicineName as taken yet."
        }

    val intent =
        Intent(
            context,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                "medicineReminderId",
                medicineId
            )
            putExtra(
                "medicineReminderTime",
                scheduledTime
            )
            putExtra(
                "navigateToHome",
                true
            )
        }

    val pendingIntent =
        PendingIntent.getActivity(
            context,

            // Avoid different doses replacing one another
            "$medicineId-$scheduledTime"
                .hashCode(),

            intent,

            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

    val notification =
        NotificationCompat.Builder(
            context,
            MEDICINE_CHANNEL_ID
        )
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

    if (
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
            "$medicineId-$scheduledTime"
                .hashCode(),
            notification
        )
}