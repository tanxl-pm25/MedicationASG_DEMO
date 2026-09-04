package com.example.medication_demo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medication_demo.storage.CurrentUserStorage
import com.example.medication_demo.storage.MedicineLocalStorage
import java.time.LocalDate

class MedicineRepeatReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val userId =
            intent.getStringExtra("userId")
                ?: return

        val currentUserId =
            CurrentUserStorage.getUserId(
                context
            )

        if (currentUserId != userId) {
            return
        }
        val medicineId =
            intent.getIntExtra(
                "medicineId",
                -1
            )

        val medicineName =
            intent.getStringExtra(
                "medicineName"
            ) ?: return

        val scheduledTime =
            intent.getStringExtra(
                "scheduledTime"
            ) ?: return

        val doseIndex =
            intent.getIntExtra(
                "doseIndex",
                -1
            )

        val doseDateText =
            intent.getStringExtra(
                "doseDate"
            ) ?: return

        val repeatNumber =
            intent.getIntExtra(
                "repeatNumber",
                0
            )

        val repeatIntervalMinutes =
            intent.getIntExtra(
                "repeatIntervalMinutes",
                0
            )

        val repeatCount =
            intent.getIntExtra(
                "repeatCount",
                0
            )

        if (
            medicineId == -1 ||
            doseIndex == -1
        ) {
            return
        }

        val doseDate =
            try {
                LocalDate.parse(
                    doseDateText
                )
            } catch (_: Exception) {
                return
            }

        val localStorage =
            MedicineLocalStorage(
                context = context,
                userId = userId
            )

        val takenRecords =
            localStorage.loadTakenRecords()

        val alreadyTaken =
            takenRecords.any { record ->

                record.medicineId ==
                        medicineId &&

                        record.date ==
                        doseDate &&

                        record.doseIndex ==
                        doseIndex
            }

        // User already took this dose.
        // Stop the repeat reminder chain.
        if (alreadyTaken) {
            return
        }

        showMedicineNotification(
            context = context,
            medicineId = medicineId,
            medicineName = medicineName,
            scheduledTime = scheduledTime,
            repeatNumber = repeatNumber
        )

        // Schedule next repeat reminder.
        if (
            repeatNumber < repeatCount &&
            repeatIntervalMinutes > 0
        ) {
            scheduleMedicineRepeat(
                context = context,
                userId = userId,
                medicineId = medicineId,
                medicineName = medicineName,
                scheduledTime = scheduledTime,
                doseIndex = doseIndex,
                doseDate = doseDate,
                repeatIntervalMinutes =
                    repeatIntervalMinutes,
                repeatCount = repeatCount,
                repeatNumber =
                    repeatNumber + 1
            )
        }
    }
}