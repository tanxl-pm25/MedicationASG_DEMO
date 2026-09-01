package com.example.medication_demo.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RefillReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        val medicineName =
            inputData.getString(
                KEY_MEDICINE_NAME
            ) ?: "Medicine"

        val tabletsLeft =
            inputData.getInt(
                KEY_TABLETS_LEFT,
                0
            )

        val medicineId =
            inputData.getInt(
                KEY_MEDICINE_ID,
                -1
            )

        if (medicineId == -1) {
            return Result.failure()
        }

        showRefillNotification(
            context = applicationContext,
            medicineId = medicineId,
            medicineName = medicineName,
            tabletsLeft = tabletsLeft
        )

        return Result.success()
    }

    companion object {
        const val KEY_MEDICINE_ID =
            "medicine_id"
        const val KEY_MEDICINE_NAME =
            "medicine_name"

        const val KEY_TABLETS_LEFT =
            "tablets_left"
    }
}