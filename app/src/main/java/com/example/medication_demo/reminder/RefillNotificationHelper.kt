package com.example.medication_demo.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medication_demo.R
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.medication_demo.MainActivity

private const val REFILL_CHANNEL_ID = "refill_reminder_channel"

fun createRefillNotificationChannel(
    context: Context
) {

    val soundUri =
        android.media.RingtoneManager
            .getDefaultUri(
                android.media.RingtoneManager.TYPE_NOTIFICATION
            )

    val audioAttributes =
        android.media.AudioAttributes.Builder()
            .setUsage(
                android.media.AudioAttributes.USAGE_NOTIFICATION
            )
            .build()

    val channel =
        NotificationChannel(
            REFILL_CHANNEL_ID,
            "Refill Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {

            description =
                "Notifications for low medicine quantity and refill reminders"

            enableVibration(true)

            setSound(
                soundUri,
                audioAttributes
            )
        }

    val manager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    manager.createNotificationChannel(
        channel
    )
}


fun showRefillNotification(
    context: Context,
    medicineId: Int,
    medicineName: String,
    tabletsLeft: Int
) {

    val intent =
        Intent(
            context,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                "refillMedicineId",
                medicineId
            )
        }

    val pendingIntent =
        PendingIntent.getActivity(
            context,
            medicineId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

    val notification =
        NotificationCompat.Builder(
            context,
            REFILL_CHANNEL_ID
        )
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .setContentTitle(
                "Running Low!"
            )
            .setContentText(
                "$medicineName has $tabletsLeft tablets left."
            )
            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )
            .setAutoCancel(true)

            // 點 notification 後打開 App
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
            medicineId,
            notification
        )
}