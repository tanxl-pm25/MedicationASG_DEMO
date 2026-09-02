package com.example.medication_demo.storage

import android.content.Context
import com.example.medication_demo.model.Medicine
import kotlinx.serialization.json.Json
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.MedicationTakenRecordStorage
import java.time.LocalDate
import com.example.medication_demo.model.ArchivedMedicine
import com.example.medication_demo.model.ArchivedMedicineStorage
import java.time.LocalTime
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.model.RescheduledDoseStorage
import com.example.medication_demo.model.MedicineScheduleSnapshot
import com.example.medication_demo.model.MedicineScheduleSnapshotStorage

class MedicineLocalStorage(
    context: Context,
    userId: String
) {

    private val sharedPreferences =
        context.getSharedPreferences(
            "medicine_preferences_$userId",
            Context.MODE_PRIVATE
        )

    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    //==================
    // MEDICINES
    //==================
    fun saveMedicines(
        medicines: List<Medicine>
    ) {
        val jsonString =
            json.encodeToString(medicines)

        sharedPreferences
            .edit()
            .putString(
                KEY_MEDICINES,
                jsonString
            )
            .apply()
    }

    fun loadMedicines(): List<Medicine> {

        val jsonString =
            sharedPreferences.getString(
                KEY_MEDICINES,
                null
            ) ?: return emptyList()

        return try {
            json.decodeFromString<List<Medicine>>(
                jsonString
            )
        } catch (_: Exception) {
            emptyList()
        }
    }

    //==================
    // TAKEN RECORDS
    //==================
    fun saveTakenRecords(
        records: List<MedicationTakenRecord>
    ) {

        val storageRecords =
            records.map { record ->

                MedicationTakenRecordStorage(
                    medicineId = record.medicineId,
                    date = record.date.toString(),
                    doseIndex = record.doseIndex,
                    reminderTime = record.reminderTime,
                    takenTime = record.takenTime,
                    dosageAmount = record.dosageAmount,
                    dosageType = record.dosageType
                )
            }

        val jsonString =
            json.encodeToString(storageRecords)

        sharedPreferences
            .edit()
            .putString(
                KEY_TAKEN_RECORDS,
                jsonString
            )
            .apply()
    }

    fun loadTakenRecords():
            List<MedicationTakenRecord> {

        val jsonString =
            sharedPreferences.getString(
                KEY_TAKEN_RECORDS,
                null
            ) ?: return emptyList()

        return try {

            val storageRecords =
                json.decodeFromString<
                        List<MedicationTakenRecordStorage>
                        >(jsonString)

            storageRecords.map { record ->

                MedicationTakenRecord(
                    medicineId = record.medicineId,
                    date = LocalDate.parse(record.date),
                    doseIndex = record.doseIndex,
                    reminderTime = record.reminderTime,
                    takenTime = record.takenTime,
                    dosageAmount = record.dosageAmount,
                    dosageType = record.dosageType
                )
            }

        } catch (_: Exception) {
            emptyList()
        }
    }

    //=====================
    // ARCHIVED MEDICINES
    //=====================
    fun saveArchivedMedicines(
        archivedMedicines: List<ArchivedMedicine>
    ) {

        val storageList =
            archivedMedicines.map { archived ->

                ArchivedMedicineStorage(
                    medicine = archived.medicine,
                    deletedDate = archived.deletedDate.toString(),
                    deletedTime = archived.deletedTime.toString()
                )
            }

        val jsonString =
            json.encodeToString(storageList)

        sharedPreferences
            .edit()
            .putString(
                KEY_ARCHIVED_MEDICINES,
                jsonString
            )
            .apply()
    }

    fun loadArchivedMedicines():
            List<ArchivedMedicine> {

        val jsonString =
            sharedPreferences.getString(
                KEY_ARCHIVED_MEDICINES,
                null
            ) ?: return emptyList()

        return try {

            val storageList =
                json.decodeFromString<
                        List<ArchivedMedicineStorage>
                        >(jsonString)

            storageList.map { archived ->

                ArchivedMedicine(
                    medicine = archived.medicine,
                    deletedDate =
                        LocalDate.parse(
                            archived.deletedDate
                        ),
                    deletedTime =
                        LocalTime.parse(
                            archived.deletedTime
                        )
                )
            }

        } catch (_: Exception) {
            emptyList()
        }
    }

    //=====================
// REMAINING QUANTITIES
//=====================
    fun saveRemainingQuantities(
        quantities: Map<Int, Double>
    ) {

        val jsonString =
            json.encodeToString(quantities)

        sharedPreferences
            .edit()
            .putString(
                KEY_REMAINING_QUANTITIES,
                jsonString
            )
            .apply()
    }

    fun loadRemainingQuantities():
            Map<Int, Double> {

        val jsonString =
            sharedPreferences.getString(
                KEY_REMAINING_QUANTITIES,
                null
            ) ?: return emptyMap()

        return try {

            json.decodeFromString<Map<Int, Double>>(
                jsonString
            )

        } catch (_: Exception) {
            emptyMap()
        }
    }

    //=====================
    // RESCHEDULED DOSES
    //=====================
    fun saveRescheduledDoses(
        doses: List<RescheduledDose>
    ) {

        val storageList =
            doses.map { dose ->

                RescheduledDoseStorage(
                    medicineId = dose.medicineId,
                    date = dose.date.toString(),
                    doseIndex = dose.doseIndex,
                    originalTime = dose.originalTime,
                    newTime = dose.newTime
                )
            }

        val jsonString =
            json.encodeToString(storageList)

        sharedPreferences
            .edit()
            .putString(
                KEY_RESCHEDULED_DOSES,
                jsonString
            )
            .apply()
    }

    fun loadRescheduledDoses():
            List<RescheduledDose> {

        val jsonString =
            sharedPreferences.getString(
                KEY_RESCHEDULED_DOSES,
                null
            ) ?: return emptyList()

        return try {

            val storageList =
                json.decodeFromString<
                        List<RescheduledDoseStorage>
                        >(jsonString)

            storageList.map { dose ->

                RescheduledDose(
                    medicineId = dose.medicineId,
                    date = LocalDate.parse(dose.date),
                    doseIndex = dose.doseIndex,
                    originalTime = dose.originalTime,
                    newTime = dose.newTime
                )
            }

        } catch (_: Exception) {
            emptyList()
        }
    }

    //=====================
// SCHEDULE SNAPSHOTS
//=====================
    fun saveScheduleSnapshots(
        snapshots: List<MedicineScheduleSnapshot>
    ) {

        val storageList =
            snapshots.map { snapshot ->

                MedicineScheduleSnapshotStorage(
                    medicineId = snapshot.medicineId,
                    effectiveDate = snapshot.effectiveDate.toString(),

                    name = snapshot.name,
                    quantity = snapshot.quantity,
                    dosageAmount = snapshot.dosageAmount,
                    dosageType = snapshot.dosageType,

                    frequency = snapshot.frequency,
                    reminderTimes = snapshot.reminderTimes,
                    startDate = snapshot.startDate,

                    presetImageRes = snapshot.presetImageRes,
                    galleryImageUri = snapshot.galleryImageUri
                )
            }

        val jsonString =
            json.encodeToString(storageList)

        sharedPreferences
            .edit()
            .putString(
                KEY_SCHEDULE_SNAPSHOTS,
                jsonString
            )
            .apply()
    }

    fun loadScheduleSnapshots():
            List<MedicineScheduleSnapshot> {

        val jsonString =
            sharedPreferences.getString(
                KEY_SCHEDULE_SNAPSHOTS,
                null
            ) ?: return emptyList()

        return try {

            val storageList =
                json.decodeFromString<
                        List<MedicineScheduleSnapshotStorage>
                        >(jsonString)

            storageList.map { snapshot ->

                MedicineScheduleSnapshot(
                    medicineId = snapshot.medicineId,
                    effectiveDate =
                        LocalDate.parse(
                            snapshot.effectiveDate
                        ),

                    name = snapshot.name,
                    quantity = snapshot.quantity,
                    dosageAmount = snapshot.dosageAmount,
                    dosageType = snapshot.dosageType,

                    frequency = snapshot.frequency,
                    reminderTimes = snapshot.reminderTimes,
                    startDate = snapshot.startDate,

                    presetImageRes = snapshot.presetImageRes,
                    galleryImageUri = snapshot.galleryImageUri
                )
            }

        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val KEY_MEDICINES = "medicines"
        private const val KEY_TAKEN_RECORDS = "taken_records"
        private const val KEY_ARCHIVED_MEDICINES = "archived_medicines"
        private const val KEY_REMAINING_QUANTITIES = "remaining_quantities"
        private const val KEY_RESCHEDULED_DOSES = "rescheduled_doses"
        private const val KEY_SCHEDULE_SNAPSHOTS = "schedule_snapshots"

    }
}