package com.example.medication_demo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medication_demo.storage.CurrentUserStorage
import com.example.medication_demo.storage.MedicineLocalStorage
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime

class MedicineBootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            intent.action !=
            Intent.ACTION_BOOT_COMPLETED
        ) {
            return
        }

        // Get the last authenticated user
        val userId =
            CurrentUserStorage
                .getUserId(context)
                ?: return

        // Load this user's local medicines
        val localStorage =
            MedicineLocalStorage(
                context = context,
                userId = userId
            )

        val medicines =
            localStorage.loadMedicines()

        val nowDateTime =
            getMalaysiaDate()
                .atTime(
                    getMalaysiaTime()
                )

        medicines.forEach { medicine ->

            if (!medicine.reminderEnabled) {
                return@forEach
            }

            val nextDose =
                findNextScheduledDose(
                    medicine = medicine,
                    afterDateTime = nowDateTime
                )
                    ?: return@forEach

            scheduleMedicineAlarm(
                context = context,
                userId = userId,
                medicineId = medicine.id,
                medicineName = medicine.name,
                doseIndex = nextDose.doseIndex,
                doseDate = nextDose.doseDate,
                scheduledTime = nextDose.scheduledTime,
                scheduledLocalTime =
                    nextDose.scheduledLocalTime,
                repeatReminderEnabled =
                    medicine.repeatReminderEnabled,
                repeatIntervalMinutes =
                    medicine.repeatIntervalMinutes,
                repeatCount =
                    medicine.repeatCount
            )
        }
    }
}