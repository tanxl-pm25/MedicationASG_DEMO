package com.example.medication_demo.reminder

import com.example.medication_demo.model.MedicationMissedRecord
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.medication_demo.storage.MedicineLocalStorage
import java.time.LocalDate

class MedicineReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(
    appContext,
    workerParams
) {

    override fun doWork(): Result {
        val userId =
            inputData.getString(
                KEY_USER_ID
            ) ?: return Result.failure()

        val medicineId =
            inputData.getInt(
                KEY_MEDICINE_ID,
                -1
            )

        val medicineName =
            inputData.getString(
                KEY_MEDICINE_NAME
            ) ?: return Result.failure()

        val scheduledTime =
            inputData.getString(
                KEY_SCHEDULED_TIME
            ) ?: return Result.failure()

        val doseIndex =
            inputData.getInt(
                KEY_DOSE_INDEX,
                -1
            )

        val doseDateText =
            inputData.getString(
                KEY_DOSE_DATE
            ) ?: return Result.failure()

        val repeatNumber =
            inputData.getInt(
                KEY_REPEAT_NUMBER,
                0
            )

        val repeatIntervalMinutes =
            inputData.getInt(
                KEY_REPEAT_INTERVAL_MINUTES,
                0
            )

        val repeatCount =
            inputData.getInt(
                KEY_REPEAT_COUNT,
                0
            )

        if (
            medicineId == -1 || doseIndex == -1
        ) {
            return Result.failure()
        }

        val doseDate =
            try {
                LocalDate.parse(doseDateText)
            } catch (_: Exception) {
                return Result.failure()
            }

        val localStorage =
            MedicineLocalStorage(
                applicationContext,
                userId
            )

        val takenRecords = localStorage.loadTakenRecords()

        val alreadyTaken =
            takenRecords.any { record ->
                record.medicineId == medicineId && record.date == doseDate && record.doseIndex == doseIndex
            }

        if (alreadyTaken) {
            return Result.success()
        }

        val medicine =
            localStorage.loadMedicines()
                .firstOrNull {
                    it.id == medicineId
                }
                ?: return Result.success()

        MedicationNotification.show(
            context = applicationContext,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = scheduledTime,
            medicineName = medicineName,
            dosage =
                "${medicine.dosageAmount} " +
                        medicine.dosageType
        )

        if (repeatNumber > repeatCount) {
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
                context = applicationContext,
                medicineId = medicineId,
                doseIndex = doseIndex,
                originalTime = scheduledTime,
                medicineName = medicineName,
                dosage =
                    "${medicine.dosageAmount} " +
                            medicine.dosageType
            )

            return Result.success()
        }

        MedicationNotification.show(
            context = applicationContext,
            medicineId = medicineId,
            doseIndex = doseIndex,
            originalTime = scheduledTime,
            medicineName = medicineName,
            dosage =
                "${medicine.dosageAmount} " +
                        medicine.dosageType
        )

        if (
            repeatNumber <= repeatCount &&
            repeatIntervalMinutes > 0
        ) {
            scheduleMedicineRepeat(
                context = applicationContext,
                userId = userId,
                medicineId = medicineId,
                medicineName = medicineName,
                scheduledTime = scheduledTime,
                doseIndex = doseIndex,
                doseDate = doseDate,
                repeatIntervalMinutes =
                    if (repeatNumber == repeatCount) {
                        1
                    } else {
                        repeatIntervalMinutes
                    },
                repeatCount = repeatCount,
                repeatNumber = repeatNumber + 1
            )
        }
        return Result.success()
    }

    companion object {
        const val KEY_MEDICINE_ID = "medicine_id"
        const val KEY_MEDICINE_NAME = "medicine_name"
        const val KEY_SCHEDULED_TIME = "scheduled_time"
        const val KEY_DOSE_INDEX = "dose_index"
        const val KEY_DOSE_DATE = "dose_date"
        const val KEY_REPEAT_NUMBER = "repeat_number"
        const val KEY_USER_ID = "user_id"
        const val KEY_REPEAT_INTERVAL_MINUTES = "repeat_interval_minutes"
        const val KEY_REPEAT_COUNT = "repeat_count"
    }
}