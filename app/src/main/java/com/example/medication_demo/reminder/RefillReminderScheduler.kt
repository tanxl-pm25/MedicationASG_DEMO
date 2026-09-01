package com.example.medication_demo.reminder

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleRefillReminder(
    context: Context,
    medicineId: Int,
    medicineName: String,
    tabletsLeft: Int,
    delayMinutes: Int
) {

    val inputData =
        Data.Builder()
            .putInt(
                RefillReminderWorker.KEY_MEDICINE_ID,
                medicineId
            )
            .putString(
                RefillReminderWorker.KEY_MEDICINE_NAME,
                medicineName
            )
            .putInt(
                RefillReminderWorker.KEY_TABLETS_LEFT,
                tabletsLeft
            )
            .build()

    val request =
        OneTimeWorkRequestBuilder<
                RefillReminderWorker
                >()
            .setInitialDelay(
                delayMinutes.toLong(),
                TimeUnit.MINUTES
            )
            .setInputData(inputData)
            .build()

    WorkManager
        .getInstance(context)
        .enqueue(request)
}