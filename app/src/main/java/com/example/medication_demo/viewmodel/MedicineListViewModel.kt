package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MedicineDoseUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.model.MedicineStatus
import com.example.medication_demo.model.NextMedicineDose
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime

class MedicineListViewModel : ViewModel() {
    private val malaysiaZone = java.time.ZoneId.of("Asia/Kuala_Lumpur")

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    fun onSearchTextChange(value: String) {
        _searchText.value = value
    }

    fun onFilterSelected(value: String) {
        _selectedFilter.value = value
    }

    fun calculateEndDate(medicine: Medicine): LocalDate? {
        val startDate = try {
            LocalDate.parse(
                medicine.startDate,
                dateFormatter
            )
        } catch (_: Exception) {
            return null
        }
        val quantity =
            medicine.quantity.toDoubleOrNull()
                ?: return null
        val dosage =
            medicine.dosageAmount.toDoubleOrNull()
                ?: return null

        if (quantity <= 0 || dosage <= 0) {
            return null
        }
        // Total number of times the medicine can be taken
        val totalDoses =
            kotlin.math.ceil(
                quantity / dosage
            ).toLong()

        if (totalDoses <= 1) {
            return startDate
        }

        return when (medicine.frequency) {

            "Once a day" -> {
                startDate.plusDays(
                    totalDoses - 1
                )
            }

            "Twice a day" -> {

                val totalHours =
                    (totalDoses - 1) * 12

                startDate.plusDays(
                    totalHours / 24
                )
            }

            "3 times a day" -> {

                val totalHours =
                    (totalDoses - 1) * 8

                startDate.plusDays(
                    totalHours / 24
                )
            }

            "Once a week" -> {
                startDate.plusWeeks(
                    totalDoses - 1
                )
            }

            "As needed" -> {
                null
            }

            else -> {
                calculateCustomEndDate(
                    medicine = medicine,
                    startDate = startDate,
                    totalDoses = totalDoses
                )
            }
        }
    }

    fun isMedicineActiveOnDate(
        medicine: Medicine,
        date: LocalDate
    ): Boolean {
        val startDate = try {
            LocalDate.parse(
                medicine.startDate,
                dateFormatter
            )
        } catch (_: Exception) {
            return false
        }
        val endDate = calculateEndDate(medicine)
        val hasStarted = !date.isBefore(startDate)
        val hasNotEnded = endDate == null || !date.isAfter(endDate)
        return hasStarted && hasNotEnded
    }

    private fun calculateCustomEndDate(
        medicine: Medicine,
        startDate: LocalDate,
        totalDoses: Long
    ): LocalDate? {

        val frequency = medicine.frequency
            .trim()
            .lowercase()

        val regex = Regex(
            """every\s+(\d+)\s+(hour|hours|day|days|week|weeks|month|months)"""
        )

        val match = regex.find(frequency)
            ?: return null

        val amount = match.groupValues[1].toLongOrNull()
            ?: return null

        val unit = match.groupValues[2]

        if (amount <= 0) {
            return null
        }

        return when (unit) {

            "hour", "hours" -> {
                val totalHours =
                    (totalDoses - 1) * amount

                // Round upward because part of a day
                // still means the medicine ends on that date
                val totalDays =
                    (totalHours + 23) / 24

                startDate.plusDays(totalDays)
            }

            "day", "days" -> {
                startDate.plusDays(
                    (totalDoses - 1) * amount
                )
            }

            "week", "weeks" -> {
                startDate.plusWeeks(
                    (totalDoses - 1) * amount
                )
            }

            "month", "months" -> {
                startDate.plusMonths(
                    (totalDoses - 1) * amount
                )
            }

            else -> null
        }
    }

    fun filterMedicines(
        medicines: List<Medicine>,
        searchText: String,
        selectedFilter: String
    ): List<Medicine> {

        return medicines.filter { medicine ->

            val matchesSearch = medicine.name.contains(
                    searchText,
                    ignoreCase = true
                )
            val status = getMedicineStatus(medicine)

            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Active" -> status == MedicineStatus.ACTIVE
                "Upcoming" -> status == MedicineStatus.UPCOMING
                "Completed" -> status == MedicineStatus.COMPLETED
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    fun getMedicineStatus(medicine: Medicine): MedicineStatus {
        val startDate = try {
            LocalDate.parse(
                medicine.startDate,
                dateFormatter
            )
        } catch (_: Exception) {
            return MedicineStatus.ACTIVE
        }

        val today = getMalaysiaDate()

        // Not started yet
        if (today.isBefore(startDate)) {
            return MedicineStatus.UPCOMING
        }
        val endDate = calculateEndDate(medicine)

        // Fixed schedule and already passed end date
        if (
            endDate != null &&
            today.isAfter(endDate)
        ) {
            return MedicineStatus.COMPLETED
        }
        return MedicineStatus.ACTIVE
    }

    fun getNextMedicineDose(
        medicines: List<Medicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): NextMedicineDose? {
        val today = getMalaysiaDate()
        val timeFormatter =
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )
        val doses =
            medicines
                .filter { medicine ->
                    isMedicineActiveOnDate(
                        medicine = medicine,
                        date = today
                    )
                }
                .flatMap { medicine ->
                    medicine.reminderTimes.mapNotNull { reminder ->
                        val doseStatus =
                            getDoseStatus(
                                medicineId = medicine.id,
                                reminderTimeText = reminder.time,
                                date = today,
                                takenRecords = takenRecords,
                                rescheduledDoses = rescheduledDoses
                            )
                        if (doseStatus == DoseStatus.TAKEN) {
                            null
                        } else {

                            val effectiveTime =
                                getEffectiveReminderTime(
                                    medicineId = medicine.id,
                                    originalTime = reminder.time,
                                    date = today,
                                    rescheduledDoses = rescheduledDoses
                                )

                            val parsedTime = try {
                                java.time.LocalTime.parse(
                                    effectiveTime,
                                    timeFormatter
                                )
                            } catch (_: Exception) {
                                null
                            }

                            if (parsedTime == null) {
                                null
                            } else {
                                Pair(
                                    parsedTime,
                                    NextMedicineDose(
                                        medicineId = medicine.id,
                                        medicineName = medicine.name,
                                        dosage = getDosageText(medicine),
                                        reminderTime = effectiveTime,
                                        originalTime = reminder.time,
                                        status = doseStatus
                                    )
                                )
                            }
                        }
                    }
                }
        val missingDose =
            doses
                .filter {
                    it.second.status == DoseStatus.MISSING
                }
                .minByOrNull {
                    it.first
                }
        val upcomingDose =
            doses
                .filter {
                    it.second.status == DoseStatus.UPCOMING
                }
                .minByOrNull {
                    it.first
                }
        return missingDose?.second
            ?: upcomingDose?.second
    }

    fun getEffectiveReminderTime(
        medicineId: Int,
        originalTime: String,
        date: LocalDate,
        rescheduledDoses: List<RescheduledDose>
    ): String {

        return rescheduledDoses
            .find { dose ->
                dose.medicineId == medicineId &&
                        dose.date == date &&
                        dose.originalTime == originalTime
            }
            ?.newTime
            ?: originalTime
    }

    fun getDoseStatus(
        medicineId: Int,
        reminderTimeText: String,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): DoseStatus {

        val isTaken =
            takenRecords.any { record ->
                record.medicineId == medicineId &&
                        record.date == date &&
                        record.reminderTime == reminderTimeText
            }

        if (isTaken) {
            return DoseStatus.TAKEN
        }

        val today = getMalaysiaDate()
        if (date.isBefore(today)) {
            return DoseStatus.MISSING
        }

        if (date.isAfter(today)) {
            return DoseStatus.UPCOMING
        }

        val effectiveTimeText =
            getEffectiveReminderTime(
                medicineId = medicineId,
                originalTime = reminderTimeText,
                date = date,
                rescheduledDoses = rescheduledDoses
            )

        val reminderTime = try {
            java.time.LocalTime.parse(
                effectiveTimeText,
                DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    Locale.ENGLISH
                )
            )
        } catch (_: Exception) {
            return DoseStatus.UPCOMING
        }

        return if (
            getMalaysiaTime().isAfter(reminderTime)
        ) {
            DoseStatus.MISSING
        } else {
            DoseStatus.UPCOMING
        }
    }

    fun getDateDoseStatus(
        medicines: List<Medicine>,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): DoseStatus? {

        val dosesForDate =
            medicines
                .filter { medicine ->
                    isMedicineActiveOnDate(
                        medicine = medicine,
                        date = date
                    )
                }
                .flatMap { medicine ->
                    createMedicineDoseUiList(
                        medicine = medicine,
                        date = date,
                        takenRecords = takenRecords,
                        rescheduledDoses = rescheduledDoses
                    )
                }

        return when {
            dosesForDate.isEmpty() ->
                null

            dosesForDate.all {
                it.status == DoseStatus.TAKEN
            } ->
                DoseStatus.TAKEN

            dosesForDate.any {
                it.status == DoseStatus.MISSING
            } ->
                DoseStatus.MISSING

            else ->
                DoseStatus.UPCOMING
        }
    }

    fun getMedicineDosesForDate(
        medicine: Medicine,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): List<MedicineDoseUi> {

        if (
            !isMedicineActiveOnDate(
                medicine = medicine,
                date = date
            )
        ) {
            return emptyList()
        }

        return createMedicineDoseUiList(
            medicine = medicine,
            date = date,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses
        )
    }

    fun createMedicineDoseUiList(
        medicine: Medicine,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): List<MedicineDoseUi> {

        val dosageText =
            getDosageText(medicine)

        return medicine.reminderTimes.map { reminder ->

            val effectiveTime =
                getEffectiveReminderTime(
                    medicineId = medicine.id,
                    originalTime = reminder.time,
                    date = date,
                    rescheduledDoses = rescheduledDoses
                )

            val doseStatus =
                getDoseStatus(
                    medicineId = medicine.id,
                    reminderTimeText = reminder.time,
                    date = date,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                )

            val wasRescheduled =
                effectiveTime != reminder.time

            MedicineDoseUi(
                time = effectiveTime,
                medicineName = medicine.name,
                dosage = dosageText,
                status = doseStatus,

                extraText =
                    if (wasRescheduled) {
                        "Rescheduled from ${reminder.time}"
                    } else {
                        null
                    }
            )
        }
    }

    fun getDosageText(
        medicine: Medicine
    ): String {
        return if (medicine.dosageAmount == "1") {
            "${medicine.dosageAmount} ${medicine.dosageType}"
        } else {
            "${medicine.dosageAmount} ${medicine.dosageType}s"
        }
    }

}

