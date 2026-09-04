package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.ArchivedMedicine
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MedicineDoseUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.model.MedicineDailyHistoryUi
import com.example.medication_demo.model.MedicineStatus
import com.example.medication_demo.model.NextMedicineDose
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

import com.example.medication_demo.utils.MEDICINE_DATE_FORMATTER
import com.example.medication_demo.utils.MEDICINE_TIME_FORMATTER
import com.example.medication_demo.utils.parseMedicineDate
import com.example.medication_demo.utils.parseMedicineTime
import com.example.medication_demo.utils.isDoseBeforeMedicineDeletion

class MedicineListViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()



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
        val startDate =
            parseMedicineDate(
                medicine.startDate
            ) ?: return null

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
        val startDate =
            parseMedicineDate(
                medicine.startDate
            ) ?: return false
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
                    ChronoUnit.DAYS.between(
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
                    ChronoUnit.DAYS.between(
                        startDate,
                        date
                    )

                daysBetween % amount == 0L
            }
            "week",
            "weeks" -> {

                val daysBetween =
                    ChronoUnit.DAYS.between(
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

                val firstReminderTime =
                    parseMedicineTime(
                        firstReminderText
                    ) ?: return null

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
        val startDate =
            parseMedicineDate(
                medicine.startDate
            ) ?: return MedicineStatus.ACTIVE

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
                    ).mapIndexedNotNull { doseIndex, originalTime ->
                        val doseStatus =
                            getDoseStatus(
                                medicine = medicine,
                                doseIndex = doseIndex,
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
                                    doseIndex = doseIndex,
                                    originalTime = originalTime,
                                    date = today,
                                    rescheduledDoses = rescheduledDoses
                                )

                            val parsedTime =
                                parseMedicineTime(
                                    effectiveTime
                                )
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
                                        doseIndex = doseIndex,
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
                    dosage = getTakenDosageText(record),                    status = DoseStatus.TAKEN,
                    extraText = null,
                    takenTime = record.takenTime
                )
            }
    }

    private fun getTakenDosageText(
        record: MedicationTakenRecord
    ): String {

        val amount =
            record.dosageAmount.trim()

        val type =
            record.dosageType.trim()

        return if (amount == "1") {
            "$amount ${type.removeSuffix("s")}"
        } else {
            "$amount ${
                if (type.endsWith("s")) {
                    type
                } else {
                    "${type}s"
                }
            }"
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
            val startDate =
                parseMedicineDate(
                    medicine.startDate
                ) ?: return emptyList()
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

        val startDate =
            parseMedicineDate(
                medicine.startDate
            ) ?: return emptyList()

        if (date.isBefore(startDate)) {
            return emptyList()
        }

        val firstReminderText =
            medicine.reminderTimes
                .firstOrNull()
                ?.time
                ?: return emptyList()

        val firstReminderTime =
            parseMedicineTime(
                firstReminderText
            ) ?: return emptyList()

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
                        .format(MEDICINE_TIME_FORMATTER)
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
        doseIndex: Int,
        originalTime: String,
        date: LocalDate,
        rescheduledDoses: List<RescheduledDose>
    ): String {
        return rescheduledDoses
            .find { dose ->
                dose.medicineId == medicineId &&
                        dose.date == date &&
                        dose.doseIndex == doseIndex
            }
            ?.newTime
            ?: originalTime
    }

    fun getDoseStatus(
        medicine: Medicine,
        doseIndex: Int,
        reminderTimeText: String,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): DoseStatus {

        val isTaken =
            takenRecords.any { record ->
                record.medicineId == medicine.id &&
                        record.date == date &&
                        record.doseIndex == doseIndex
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
                medicineId = medicine.id,
                doseIndex = doseIndex,
                originalTime = reminderTimeText,
                date = date,
                rescheduledDoses = rescheduledDoses
            )

        val reminderTime =
            parseMedicineTime(
                effectiveTimeText
            ) ?: return DoseStatus.UPCOMING

        val currentMinute =
            getMalaysiaTime()
                .withSecond(0)
                .withNano(0)

        // Reminder time has not arrived yet
        if (currentMinute.isBefore(reminderTime)) {
            return DoseStatus.UPCOMING
        }

        // Repeat Reminder is enabled
        if (
            medicine.repeatReminderEnabled &&
            medicine.repeatIntervalMinutes > 0 &&
            medicine.repeatCount > 0
        ) {

            val repeatWindowMinutes =
                medicine.repeatIntervalMinutes.toLong() *
                        medicine.repeatCount.toLong()

            val missingDeadline =
                reminderTime.plusMinutes(
                    repeatWindowMinutes
                )

            // Keep the dose in progress until the final repeat reminder finishes
            if (!currentMinute.isAfter(missingDeadline)) {
                return DoseStatus.IN_PROGRESS
            }

            return DoseStatus.MISSING
        }

        // Repeat Reminder is OFF:
        // keep your original behaviour
        return if (
            currentMinute == reminderTime
        ) {
            DoseStatus.IN_PROGRESS
        } else {
            DoseStatus.MISSING
        }
    }

    fun getEffectiveDosesForDate(
        medicines: List<Medicine>,
        archivedMedicines: List<ArchivedMedicine>,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>,
        medicineVm: MedicineViewModel
    ): List<MedicineDoseUi> {

        val activeDoses =
            medicines
                .filter { medicine ->
                    !medicine.frequency.equals(
                        "As needed",
                        ignoreCase = true
                    ) &&
                            isMedicineActiveOnDate(
                                medicine = medicine,
                                date = date
                            )
                }
                .flatMap { medicine ->
                    getMedicineDosesForDate(
                        medicine = medicine,
                        date = date,
                        takenRecords = takenRecords,
                        rescheduledDoses = rescheduledDoses
                    )
                }
        val archivedDoses =
            archivedMedicines
                .filter { archived ->
                    !date.isAfter(
                        archived.deletedDate
                    )
                }
                .flatMap { archived ->
                    val historicalMedicine =
                        medicineVm.getMedicineForHistoricalDate(
                            medicine = archived.medicine,
                            date = date
                        )
                    getMedicineDosesForDate(
                        medicine = historicalMedicine,
                        date = date,
                        takenRecords = takenRecords,
                        rescheduledDoses = rescheduledDoses
                    ).filter { dose ->
                        val isBeforeDeletion =
                            isDoseBeforeMedicineDeletion(
                                medicineId = archived.medicine.id,
                                date = date,
                                doseTime = dose.time,
                                archivedMedicines = archivedMedicines
                            )
                        val shouldKeepHistory =
                            dose.status == DoseStatus.MISSING ||
                                    dose.status == DoseStatus.TAKEN

                        isBeforeDeletion && shouldKeepHistory
                    }
                }
        return sortDosesByTime(
            activeDoses + archivedDoses
        )
    }

    fun getEffectiveDateDoseStatus(
        medicines: List<Medicine>,
        archivedMedicines: List<ArchivedMedicine>,
        date: LocalDate,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>,
        medicineVm: MedicineViewModel
    ): DoseStatus? {

        val doses =
            getEffectiveDosesForDate(
                medicines = medicines,
                archivedMedicines = archivedMedicines,
                date = date,
                takenRecords = takenRecords,
                rescheduledDoses = rescheduledDoses,
                medicineVm = medicineVm
            )

        return when {
            doses.isEmpty() ->
                null

            doses.all {
                it.status == DoseStatus.TAKEN
            } ->
                DoseStatus.TAKEN

            doses.any {
                it.status == DoseStatus.MISSING
            } ->
                DoseStatus.MISSING

            doses.any {
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

        return scheduledTimes.mapIndexed { doseIndex, originalTime ->
            val effectiveTime =
                getEffectiveReminderTime(
                    medicineId = medicine.id,
                    doseIndex = doseIndex,
                    originalTime = originalTime,
                    date = date,
                    rescheduledDoses = rescheduledDoses
                )

            val doseStatus =
                getDoseStatus(
                    medicine = medicine,
                    doseIndex = doseIndex,
                    reminderTimeText = originalTime,
                    date = date,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                )

            val takenRecord =
                takenRecords.find { record ->
                    record.medicineId == medicine.id &&
                            record.date == date &&
                            record.doseIndex == doseIndex
                }

            val wasRescheduled = effectiveTime != originalTime

            MedicineDoseUi(
                time = effectiveTime,
                medicineName = medicine.name,
                dosage =
                    if (takenRecord != null) {
                        getTakenDosageText(takenRecord)
                    } else {
                        dosageText
                    },
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

    fun getDailyHistoryForRange(
        medicine: Medicine,
        startDate: LocalDate,
        endDate: LocalDate,
        medicineVm: MedicineViewModel,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>
    ): List<MedicineDailyHistoryUi> {

        val dates =
            generateSequence(startDate) {
                it.plusDays(1)
            }
                .takeWhile {
                    !it.isAfter(endDate)
                }
                .filter {
                    !it.isAfter(getMalaysiaDate())
                }
                .toList()

        return dates.mapNotNull { date ->
            val historicalMedicine =
                medicineVm.getMedicineForHistoricalDate(
                    medicine = medicine,
                    date = date
                )

            val doses =
                getMedicineDosesForDate(
                    medicine = historicalMedicine,
                    date = date,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                ).filter { dose ->
                    val isBeforeDeletion =
                        isDoseBeforeMedicineDeletion(
                            medicineId = medicine.id,
                            date = date,
                            doseTime = dose.time,
                            archivedMedicines = medicineVm.archivedMedicines.value
                        )
                    val isHistoryStatus = dose.status == DoseStatus.TAKEN || dose.status == DoseStatus.MISSING

                    isBeforeDeletion && isHistoryStatus
                }

            if (doses.isEmpty()) {
                null
            } else {

                MedicineDailyHistoryUi(
                    date = date,
                    frequency =
                        historicalMedicine.frequency,
                    doses = doses,
                    takenCount =
                        doses.count {
                            it.status ==
                                    DoseStatus.TAKEN
                        },
                    missingCount =
                        doses.count {
                            it.status ==
                                    DoseStatus.MISSING
                        }
                )
            }
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

        return doses.sortedBy { dose ->
            parseMedicineTime(
                dose.time
            ) ?: LocalTime.MAX
        }
    }

    fun getTodayTakenCount(
        medicines: List<Medicine>,
        archivedMedicines: List<ArchivedMedicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>,
        medicineVm: MedicineViewModel
    ): Int {

        return getEffectiveDosesForDate(
            medicines = medicines,
            archivedMedicines = archivedMedicines,
            date = getMalaysiaDate(),
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses,
            medicineVm = medicineVm
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
        archivedMedicines: List<ArchivedMedicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>,
        medicineVm: MedicineViewModel
    ): Int {

        return getEffectiveDosesForDate(
            medicines = medicines,
            archivedMedicines = archivedMedicines,
            date = getMalaysiaDate(),
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses,
            medicineVm = medicineVm
        ).size
    }

    // Home screen
    fun getNextMedicineDisplayName(
        nextDose: NextMedicineDose?,
        todayTotalDoseCount: Int
    ): String {

        if (nextDose != null) {
            return nextDose.medicineName
        }

        if (todayTotalDoseCount > 0) {
            return "You've completed today's schedule"
        }

        return "-"
    }
}

