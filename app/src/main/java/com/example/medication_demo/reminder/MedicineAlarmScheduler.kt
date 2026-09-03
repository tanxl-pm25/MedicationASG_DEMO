package com.example.medication_demo.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import android.os.Build

fun scheduleMedicineAlarm(
    context: Context,
    userId: String,
    medicineId: Int,
    medicineName: String,
    doseIndex: Int,
    doseDate: LocalDate,
    scheduledTime: String,
    scheduledLocalTime: LocalTime,
    repeatReminderEnabled: Boolean,
    repeatIntervalMinutes: Int,
    repeatCount: Int
){
    val intent =
        Intent(
            context,
            MedicineAlarmReceiver::class.java
        ).apply {
            putExtra(
                "userId",
                userId
            )
            putExtra(
                "doseIndex",
                doseIndex
            )
            putExtra(
                "doseDate",
                doseDate.toString()
            )
            putExtra(
                "repeatReminderEnabled",
                repeatReminderEnabled
            )
            putExtra(
                "repeatIntervalMinutes",
                repeatIntervalMinutes
            )
            putExtra(
                "repeatCount",
                repeatCount
            )
            putExtra(
                "medicineId",
                medicineId
            )
            putExtra(
                "medicineName",
                medicineName
            )
            putExtra(
                "scheduledTime",
                scheduledTime
            )
        }

    val requestCode =
        "medicine_alarm_$medicineId"
            .hashCode()

    val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

    val alarmManager =
        context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

    val triggerMillis =
        doseDate
            .atTime(
                scheduledLocalTime
            )
            .atZone(
                ZoneId.of(
                    "Asia/Kuala_Lumpur"
                )
            )
            .toInstant()
            .toEpochMilli()

    if (
        Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.S
    ) {
        if (
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
    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
        )
    }
}

fun cancelMedicineAlarm(
    context: Context,
    medicineId: Int
) {
    val intent =
        Intent(
            context,
            MedicineAlarmReceiver::class.java
        )

    val requestCode =
        "medicine_alarm_$medicineId"
            .hashCode()

    val pendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or
                    PendingIntent.FLAG_IMMUTABLE
        )

    if (pendingIntent != null) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(
            pendingIntent
        )

        pendingIntent.cancel()
    }
}

fun openExactAlarmSettings(
    context: Context
) {
    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        val intent =
            Intent(
                android.provider.Settings
                    .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            ).apply {
                data =
                    android.net.Uri.parse(
                        "package:${context.packageName}"
                    )
            }

        context.startActivity(intent)
    }
}

