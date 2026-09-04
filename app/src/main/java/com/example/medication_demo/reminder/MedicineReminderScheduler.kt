package com.example.medication_demo.reminder

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.time.LocalDate
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.utils.parseMedicineTime
import android.app.AlarmManager

private fun getMedicineRepeatRequestCode(
    userId: String,
    medicineId: Int,
    doseIndex: Int,
    repeatNumber: Int
): Int {
    return (
            "medicine_repeat_" +
                    "${userId}_" +
                    "${medicineId}_" +
                    "${doseIndex}_" +
                    "$repeatNumber"
            ).hashCode()
}
fun scheduleMedicineDose(
    context: Context,
    userId: String,
    medicine: Medicine,
    doseIndex: Int,
    doseDate: LocalDate,
    scheduledTime: String,
    delayMillis: Long
) {
    if (!medicine.reminderEnabled) {
        return
    }

    val scheduledLocalTime =
        parseMedicineTime(
            scheduledTime
        ) ?: return

    scheduleMedicineAlarm(
        context = context,
        userId = userId,
        medicineId = medicine.id,
        medicineName = medicine.name,
        doseIndex = doseIndex,
        doseDate = doseDate,
        scheduledTime = scheduledTime,
        scheduledLocalTime = scheduledLocalTime,
        repeatReminderEnabled = medicine.repeatReminderEnabled,
        repeatIntervalMinutes = medicine.repeatIntervalMinutes,
        repeatCount = medicine.repeatCount
    )
}

fun scheduleMedicineRepeat(
    context: Context,
    userId: String,
    medicineId: Int,
    medicineName: String,
    scheduledTime: String,
    doseIndex: Int,
    doseDate: LocalDate,
    repeatIntervalMinutes: Int,
    repeatCount: Int,
    repeatNumber: Int
) {
    if (
        repeatIntervalMinutes <= 0 ||
        repeatCount < 0 ||
        repeatNumber > repeatCount + 1
    ) {
        return
    }

    val intent =
        Intent(
            context,
            MedicineRepeatReceiver::class.java
        ).apply {
            putExtra(
                "userId",
                userId
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
            putExtra(
                "doseIndex",
                doseIndex
            )
            putExtra(
                "doseDate",
                doseDate.toString()
            )
            putExtra(
                "repeatNumber",
                repeatNumber
            )
            putExtra(
                "repeatIntervalMinutes",
                repeatIntervalMinutes
            )
            putExtra(
                "repeatCount",
                repeatCount
            )
        }

    val requestCode =
        getMedicineRepeatRequestCode(
            userId = userId,
            medicineId = medicineId,
            doseIndex = doseIndex,
            repeatNumber = repeatNumber
        )

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
        System.currentTimeMillis() +
                TimeUnit.MINUTES.toMillis(
                    repeatIntervalMinutes.toLong()
                )

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



fun cancelMedicineReminders(
    context: Context,
    userId: String,
    medicineId: Int
) {
    // Cancel old WorkManager repeats.
    // Keep temporarily for users upgrading
    // from the previous implementation.
    WorkManager
        .getInstance(context)
        .cancelAllWorkByTag(
            "medicine_${userId}_$medicineId"
        )

    // Cancel new AlarmManager repeat reminders
    cancelMedicineRepeatAlarms(
        context = context,
        userId = userId,
        medicineId = medicineId
    )

    // Cancel original medicine alarm
    cancelMedicineAlarm(
        context = context,
        medicineId = medicineId
    )
}

fun cancelMedicineRepeatAlarms(
    context: Context,
    userId: String,
    medicineId: Int
) {
    val alarmManager =
        context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

    for (doseIndex in 0..23) {
        for (repeatNumber in 1..10) {

            val intent =
                Intent(
                    context,
                    MedicineRepeatReceiver::class.java
                )

            val requestCode =
                getMedicineRepeatRequestCode(
                    userId = userId,
                    medicineId = medicineId,
                    doseIndex = doseIndex,
                    repeatNumber = repeatNumber
                )

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or
                            PendingIntent.FLAG_IMMUTABLE
                )

            if (pendingIntent != null) {
                alarmManager.cancel(
                    pendingIntent
                )

                pendingIntent.cancel()
            }
        }
    }
}