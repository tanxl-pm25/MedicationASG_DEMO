package com.example.medication_demo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medication_demo.storage.CurrentUserStorage
import java.time.LocalDate
import com.example.medication_demo.storage.MedicineLocalStorage
import com.example.medication_demo.utils.parseMedicineTime

class MedicineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val userId =
            intent.getStringExtra(
                "userId"
            ) ?: return

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

        val repeatReminderEnabled =
            intent.getBooleanExtra(
                "repeatReminderEnabled",
                false
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

        // Original notification
        showMedicineNotification(
            context = context,
            medicineId = medicineId,
            medicineName = medicineName,
            scheduledTime = scheduledTime
        )
        val localStorage =
            MedicineLocalStorage(
                context = context,
                userId = userId
            )

        val medicine =
            localStorage
                .loadMedicines()
                .find {
                    it.id == medicineId
                }

        if (medicine != null) {

            val currentDoseDateTime =
                doseDate.atTime(
                    parseMedicineTime(
                        scheduledTime
                    ) ?: return
                )

            val nextDose =
                findNextScheduledDose(
                    medicine = medicine,
                    afterDateTime = currentDoseDateTime
                )

            if (nextDose != null) {
                scheduleMedicineAlarm(
                    context = context,
                    userId = userId,
                    medicineId = medicine.id,
                    medicineName = medicine.name,
                    doseIndex = nextDose.doseIndex,
                    doseDate = nextDose.doseDate,
                    scheduledTime = nextDose.scheduledTime,
                    scheduledLocalTime = nextDose.scheduledLocalTime,
                    repeatReminderEnabled = medicine.repeatReminderEnabled,
                    repeatIntervalMinutes = medicine.repeatIntervalMinutes,
                    repeatCount = medicine.repeatCount
                )
            }
        }

        // Original sent out and start to calculate Repeat #1
        if (
            repeatReminderEnabled &&
            repeatIntervalMinutes > 0 &&
            repeatCount > 0
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
                repeatNumber = 1
            )
        }
    }
}