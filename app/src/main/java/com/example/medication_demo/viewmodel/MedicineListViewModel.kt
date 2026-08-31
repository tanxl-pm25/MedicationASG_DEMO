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
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class MedicineListViewModel : ViewModel() {
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

    private fun getTotalDoses(
        medicine: Medicine
    ): Long {

        val quantity =
            medicine.quantity.toDoubleOrNull()
                ?: return 0L

        val dosage =
            medicine.dosageAmount.toDoubleOrNull()
                ?: return 0L

        if (quantity <= 0 || dosage <= 0) {
            return 0L
        }

        return (quantity / dosage).toLong()
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

        // Total number of times the medicine can be taken
        val totalDoses = getTotalDoses(medicine)

        if (totalDoses <= 0) {
            return null
        }

        if (totalDoses <= 1L) {
            return startDate
        }

        return when (medicine.frequency) {

            "Once a day" -> {
                startDate.plusDays(
                    totalDoses - 1
                )
            }

            "Twice a day" -> {
                val totalDays =
                    (totalDoses - 1) / 2

                startDate.plusDays(totalDays)
            }

            "3 times a day" -> {
                val totalDays =
                    (totalDoses - 1) / 3

                startDate.plusDays(totalDays)
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
        // Not yet start
        if (date.isBefore(startDate)) {
            return false
        }
        // Already done
        if (
            endDate != null &&
            date.isAfter(endDate)
        ) {
            return false
        }
        val frequency =
            medicine.frequency
                .trim()
                .lowercase()
        return when (frequency) {
            "once a day",
            "twice a day",
            "3 times a day" -> {
                true
            }
            "once a week" -> {
                val daysBetween =
                    java.time.temporal.ChronoUnit.DAYS.between(
                        startDate,
                        date
                    )
                daysBetween % 7 == 0L
            }
            "as needed" -> {
                true
            }
            else -> {
                isCustomFrequencyDate(
                    frequency = frequency,
                    startDate = startDate,
                    date = date
                )
            }
        }
    }

    private fun isCustomFrequencyDate(
        frequency: String,
        startDate: LocalDate,
        date: LocalDate
    ): Boolean {
        val regex =
            Regex(
                """every\s+(\d+)\s+(hour|hours|day|days|week|weeks|month|months)"""
            )
        val match =
            regex.find(frequency)
                ?: return true
        val amount =
            match.groupValues[1]
                .toLongOrNull()
                ?: return true
        if (amount <= 0) {
            return true
        }
        val unit = match.groupValues[2]
        return when (unit) {
            "hour",
            "hours" -> {
                true
            }
            "day",
            "days" -> {

                val daysBetween =
                    java.time.temporal.ChronoUnit.DAYS.between(
                        startDate,
                        date
                    )

                daysBetween % amount == 0L
            }
            "week",
            "weeks" -> {

                val daysBetween =
                    java.time.temporal.ChronoUnit.DAYS.between(
                        startDate,
                        date
                    )

                daysBetween % (amount * 7) == 0L
            }
            "month",
            "months" -> {
                val startMonth = YearMonth.from(startDate)
                val currentMonth = YearMonth.from(date)
                val monthsBetween =
                    ChronoUnit.MONTHS.between(
                        startMonth,
                        currentMonth
                    )
                if (monthsBetween % amount != 0L) {
                    false
                } else {

                    val expectedDay =
                        minOf(
                            startDate.dayOfMonth,
                            currentMonth.lengthOfMonth()
                        )

                    date.dayOfMonth == expectedDay
                }
            }
            else -> true
        }
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
            "hour",
            "hours" -> {

                val firstReminderText =
                    medicine.reminderTimes
                        .firstOrNull()
                        ?.time
                        ?: return null

                val timeFormatter =
                    DateTimeFormatter.ofPattern(
                        "hh:mm a",
                        Locale.ENGLISH
                    )

                val firstReminderTime = try {
                    java.time.LocalTime.parse(
                        firstReminderText,
                        timeFormatter
                    )
                } catch (_: Exception) {
                    return null
                }

                val firstDoseDateTime =
                    startDate.atTime(
                        firstReminderTime
                    )

                val lastDoseDateTime =
                    firstDoseDateTime.plusHours(
                        (totalDoses - 1) * amount
                    )

                lastDoseDateTime.toLocalDate()
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
                    getScheduledReminderTimesForDate(
                        medicine = medicine,
                        date = today
                    ).mapNotNull { originalTime ->                        val doseStatus =
                            getDoseStatus(
                                medicineId = medicine.id,
                                reminderTimeText = originalTime,
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
                                    originalTime = originalTime,
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
                                        originalTime = originalTime,
                                        status = doseStatus
                                    )
                                )
                            }
                        }
                    }
                }
        val inProgressDose =
            doses
                .filter {
                    it.second.status == DoseStatus.IN_PROGRESS
                }
                .minByOrNull {
                    it.first
                }
        if (inProgressDose != null) {
            return inProgressDose.second
        }
        val missingDose =
            doses
                .filter {
                    it.second.status == DoseStatus.MISSING
                }
                .minByOrNull {
                    it.first
                }
        if (missingDose != null) {
            return missingDose.second
        }
        val upcomingDose =
            doses
                .filter {
                    it.second.status == DoseStatus.UPCOMING
                }
                .minByOrNull {
                    it.first
                }
        return upcomingDose?.second
    }

    fun getAsNeededTakenDosesForDate(
        medicine: Medicine,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>
    ): List<MedicineDoseUi> {

        if (
            !medicine.frequency.equals(
                "As needed",
                ignoreCase = true
            )
        ) {
            return emptyList()
        }

        return takenRecords
            .filter { record ->
                record.medicineId == medicine.id &&
                        record.date == date
            }
            .map { record ->

                MedicineDoseUi(
                    time = record.takenTime,
                    medicineName = medicine.name,
                    dosage = getDosageText(medicine),
                    status = DoseStatus.TAKEN,
                    extraText = null,
                    takenTime = record.takenTime
                )
            }
    }

    fun getScheduledReminderTimesForDate(
        medicine: Medicine,
        date: LocalDate
    ): List<String> {

        val frequency =
            medicine.frequency
                .trim()
                .lowercase()

        if (frequency == "as needed") {
            return emptyList()
        }

        val hourlyRegex =
            Regex(
                """every\s+(\d+)\s+(hour|hours)"""
            )

        val match =
            hourlyRegex.find(frequency)

        // Not Every N Hours
        // Ctn use original user's setting reminder times
        if (match == null) {
            val reminderTimes =
                medicine.reminderTimes.map {
                    it.time
                }
            if (reminderTimes.isEmpty()) {
                return emptyList()
            }
            val totalDoses = getTotalDoses(medicine)
            if (totalDoses <= 0) {
                return emptyList()
            }
            val startDate = try {
                LocalDate.parse(
                    medicine.startDate,
                    dateFormatter
                )
            } catch (_: Exception) {
                return emptyList()
            }
            if (date.isBefore(startDate)) {
                return emptyList()
            }
            val frequency =
                medicine.frequency
                    .trim()
                    .lowercase()
            val dosesBeforeToday =
                when (frequency) {
                    "once a day" -> {
                        ChronoUnit.DAYS.between(
                            startDate,
                            date
                        )
                    }
                    "twice a day" -> {
                        ChronoUnit.DAYS.between(
                            startDate,
                            date
                        ) * 2
                    }
                    "3 times a day" -> {
                        ChronoUnit.DAYS.between(
                            startDate,
                            date
                        ) * 3
                    }
                    "once a week" -> {
                        ChronoUnit.WEEKS.between(
                            startDate,
                            date
                        )
                    }
                    else -> {
                        0L
                    }
                }
            val remainingDoses = totalDoses - dosesBeforeToday
            if (remainingDoses <= 0) {
                return emptyList()
            }

            return reminderTimes.take(
                minOf(
                    reminderTimes.size,
                    remainingDoses.toInt()
                )
            )
        }

        val intervalHours =
            match.groupValues[1]
                .toLongOrNull()
                ?: return emptyList()

        if (intervalHours <= 0) {
            return emptyList()
        }

        val startDate = try {
            LocalDate.parse(
                medicine.startDate,
                dateFormatter
            )
        } catch (_: Exception) {
            return emptyList()
        }

        if (date.isBefore(startDate)) {
            return emptyList()
        }

        val firstReminderText =
            medicine.reminderTimes
                .firstOrNull()
                ?.time
                ?: return emptyList()

        val timeFormatter =
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )

        val firstReminderTime = try {
            java.time.LocalTime.parse(
                firstReminderText,
                timeFormatter
            )
        } catch (_: Exception) {
            return emptyList()
        }

        val firstDoseDateTime = startDate.atTime(firstReminderTime)
        val currentDateStart = date.atStartOfDay()
        val currentDateEnd = date.plusDays(1).atStartOfDay()
        val totalDoses = getTotalDoses(medicine)

        if (totalDoses <= 0) {
            return emptyList()
        }

        val result = mutableListOf<String>()

        for (doseIndex in 0 until totalDoses) {

            val doseDateTime =
                firstDoseDateTime.plusHours(
                    doseIndex * intervalHours
                )

            if (
                !doseDateTime.isBefore(currentDateStart) &&
                doseDateTime.isBefore(currentDateEnd)
            ) {
                result.add(
                    doseDateTime
                        .toLocalTime()
                        .format(timeFormatter)
                )
            }

            if (
                doseDateTime.isAfter(currentDateEnd)
            ) {
                break
            }
        }

        return result
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

        val currentTime = getMalaysiaTime()

        val currentMinute =
            currentTime
                .withSecond(0)
                .withNano(0)

        return when {
            currentMinute.isBefore(reminderTime) -> {
                DoseStatus.UPCOMING
            }

            currentMinute == reminderTime -> {
                DoseStatus.IN_PROGRESS
            }

            else -> {
                DoseStatus.MISSING
            }
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

            dosesForDate.any {
                it.status == DoseStatus.IN_PROGRESS
            } ->
                DoseStatus.IN_PROGRESS

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

        // As needed no fixed schedule
        // Only press Take Now then show Taken record
        if (
            medicine.frequency.equals(
                "As needed",
                ignoreCase = true
            )
        ) {
            return getAsNeededTakenDosesForDate(
                medicine = medicine,
                date = date,
                takenRecords = takenRecords
            )
        }

        // Other normal scheduled medicine
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

        val scheduledTimes =
            getScheduledReminderTimesForDate(
                medicine = medicine,
                date = date
            )

        return scheduledTimes.map { originalTime ->

            val effectiveTime =
                getEffectiveReminderTime(
                    medicineId = medicine.id,
                    originalTime = originalTime,
                    date = date,
                    rescheduledDoses = rescheduledDoses
                )

            val doseStatus =
                getDoseStatus(
                    medicineId = medicine.id,
                    reminderTimeText = originalTime,
                    date = date,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                )

            val takenRecord =
                takenRecords.find { record ->
                    record.medicineId == medicine.id &&
                            record.date == date &&
                            record.reminderTime == originalTime
                }

            val wasRescheduled =
                effectiveTime != originalTime

            MedicineDoseUi(
                time = effectiveTime,
                medicineName = medicine.name,
                dosage = dosageText,
                status = doseStatus,
                extraText =
                    if (wasRescheduled) {
                        "Rescheduled from $originalTime"
                    } else {
                        null
                    },
                takenTime = takenRecord?.takenTime
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

    fun sortDosesByTime(
        doses: List<MedicineDoseUi>
    ): List<MedicineDoseUi> {

        val formatter =
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )

        return doses.sortedBy { dose ->
            try {
                java.time.LocalTime.parse(
                    dose.time,
                    formatter
                )
            } catch (_: Exception) {
                java.time.LocalTime.MAX
            }
        }
    }

    fun getTodayMedicineDoses(
        medicines: List<Medicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): List<MedicineDoseUi> {

        val today = getMalaysiaDate()

        return medicines
            .filter { medicine ->

                !medicine.frequency.equals(
                    "As needed",
                    ignoreCase = true
                ) &&
                        isMedicineActiveOnDate(
                            medicine = medicine,
                            date = today
                        )
            }
            .flatMap { medicine ->
                getMedicineDosesForDate(
                    medicine = medicine,
                    date = today,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                )
            }
    }

    fun getTodayTakenCount(
        medicines: List<Medicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): Int {

        return getTodayMedicineDoses(
            medicines = medicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses
        ).count {
            it.status == DoseStatus.TAKEN
        }
    }

    fun isHourlyFrequency(
        medicine: Medicine
    ): Boolean {
        return Regex(
            """every\s+\d+\s+(hour|hours)""",
            RegexOption.IGNORE_CASE
        ).containsMatchIn(
            medicine.frequency
        )
    }

    fun getReminderLabel(
        medicine: Medicine
    ): String {
        return if (isHourlyFrequency(medicine)) {
            "First Reminder"
        } else {
            "Reminder Time"
        }
    }

    fun getReminderTimeText(
        medicine: Medicine
    ): String {
        return if (medicine.reminderTimes.isEmpty()) {
            "No fixed reminder time"
        } else {
            medicine.reminderTimes.joinToString(", ") {
                it.time
            }
        }
    }

    fun getTodayTotalDoseCount(
        medicines: List<Medicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): Int {

        return getTodayMedicineDoses(
            medicines = medicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses
        ).size
    }
}

