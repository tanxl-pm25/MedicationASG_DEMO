package com.example.medication_demo.reminder

import android.content.Context
import android.os.Build
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.time.LocalDate
import androidx.work.ExistingWorkPolicy
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.utils.parseMedicineTime

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

    val inputData =
        Data.Builder()
            .putString(
                MedicineReminderWorker.KEY_USER_ID,
                userId
            )
            .putInt(
                MedicineReminderWorker.KEY_MEDICINE_ID,
                medicineId
            )
            .putString(
                MedicineReminderWorker.KEY_MEDICINE_NAME,
                medicineName
            )
            .putString(
                MedicineReminderWorker.KEY_SCHEDULED_TIME,
                scheduledTime
            )
            .putInt(
                MedicineReminderWorker.KEY_DOSE_INDEX,
                doseIndex
            )
            .putString(
                MedicineReminderWorker.KEY_DOSE_DATE,
                doseDate.toString()
            )
            .putInt(
                MedicineReminderWorker.KEY_REPEAT_NUMBER,
                repeatNumber
            )
            .putInt(
                MedicineReminderWorker.KEY_REPEAT_INTERVAL_MINUTES,
                repeatIntervalMinutes
            )
            .putInt(
                MedicineReminderWorker.KEY_REPEAT_COUNT,
                repeatCount
            )
            .build()

    val request =
        OneTimeWorkRequestBuilder<
                MedicineReminderWorker
                >()
            .setInitialDelay(
                repeatIntervalMinutes.toLong(),
                TimeUnit.MINUTES
            )
            .setInputData(
                inputData
            )
            .addTag(
                "medicine_${userId}_$medicineId"
            )
            .build()

    val uniqueWorkName =
        "medicine_repeat_" +
                "${userId}_" +
                "${medicineId}_" +
                "${doseDate}_" +
                "${doseIndex}_" +
                "$repeatNumber"

    WorkManager
        .getInstance(context)
        .enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            request
        )
}


fun cancelMedicineReminders(
    context: Context,
    userId: String,
    medicineId: Int
) {
    // Cancel repeat reminder workers
    WorkManager
        .getInstance(context)
        .cancelAllWorkByTag(
            "medicine_${userId}_$medicineId"
        )

    // Cancel original AlarmManager reminder
    cancelMedicineAlarm(
        context = context,
        medicineId = medicineId
    )
}