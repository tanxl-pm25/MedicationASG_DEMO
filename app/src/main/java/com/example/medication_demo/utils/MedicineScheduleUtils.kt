package com.example.medication_demo.utils

import com.example.medication_demo.model.ArchivedMedicine
import java.time.LocalDate

fun isDoseBeforeMedicineDeletion(
    medicineId: Int,
    date: LocalDate,
    doseTime: String,
    archivedMedicines: List<ArchivedMedicine>
): Boolean {

    val archivedMedicine =
        archivedMedicines.find { archived ->
            archived.medicine.id == medicineId
        } ?: return true

    if (date.isBefore(archivedMedicine.deletedDate)) {
        return true
    }

    if (date.isAfter(archivedMedicine.deletedDate)) {
        return false
    }

    val scheduledTime =
        parseMedicineTime(
            doseTime
        ) ?: return false

    return !scheduledTime.isAfter(
        archivedMedicine.deletedTime
    )
}
