package com.example.medication_demo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medication_demo.model.MedicationMissedRecord
import com.example.medication_demo.storage.CurrentUserStorage
import com.example.medication_demo.storage.MedicineLocalStorage
import java.time.LocalDate

class MedicineMissedReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val userId =
            intent.getStringExtra("userId") ?: return

        if (
            CurrentUserStorage.getUserId(context) != userId
        ) {
            return
        }

        val medicineId =
            intent.getIntExtra("medicineId", -1)

        val doseIndex =
            intent.getIntExtra("doseIndex", -1)

        val medicineName =
            intent.getStringExtra("medicineName") ?: return

        val scheduledTime =
            intent.getStringExtra("scheduledTime") ?: return

        val doseDate =
            try {
                LocalDate.parse(
                    intent.getStringExtra("doseDate")
                        ?: return
                )
            } catch (_: Exception) {
                return
            }

        if (
            medicineId == -1 ||
            doseIndex == -1
        ) {
            return
        }

        val localStorage =
            MedicineLocalStorage(
                context = context,
                userId = userId
            )

        val medicine =
            localStorage.loadMedicines().firstOrNull {
                it.id == medicineId
            } ?: return

        val alreadyTaken =
            localStorage.loadTakenRecords().any {
                it.medicineId == medicineId &&
                        it.date == doseDate &&
                        it.doseIndex == doseIndex
            }

        if (alreadyTaken) {
            return
        }

        val rescheduledDose =
            localStorage.loadRescheduledDoses()
                .firstOrNull {
                    it.medicineId == medicineId &&
                            it.date == doseDate &&
                            it.doseIndex == doseIndex
                }

        if (
            rescheduledDose != null &&
            rescheduledDose.newTime != scheduledTime
        ) {
            return
        }

        val missedRecords =
            localStorage.loadMissedRecords()

        val alreadyMissed =
            missedRecords.any {
                it.medicineId == medicineId &&
                        it.date == doseDate &&
                        it.doseIndex == doseIndex
            }

        if (!alreadyMissed) {
            localStorage.saveMissedRecords(
                missedRecords +
                        MedicationMissedRecord(
                            medicineId = medicineId,
                            date = doseDate,
                            doseIndex = doseIndex,
                            reminderTime = scheduledTime,
                            dosageAmount =
                                medicine.dosageAmount,
                            dosageType =
                                medicine.dosageType
                        )
            )
        }

        MedicationNotification.showMissed(
            context = context,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = scheduledTime,
            medicineName = medicineName,
            dosage =
                "${medicine.dosageAmount} ${medicine.dosageType}"
        )
    }
}