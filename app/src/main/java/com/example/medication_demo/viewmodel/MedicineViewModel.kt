package com.example.medication_demo.viewmodel

import com.example.medication_demo.model.MedicationMissedRecord
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MedicineScheduleSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.medication_demo.model.ReminderTimeUi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime
import com.example.medication_demo.model.ArchivedMedicine
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.medication_demo.storage.MedicineLocalStorage
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.repository.MedicineRepository
import kotlinx.coroutines.launch
import com.example.medication_demo.reminder.cancelMedicineReminders
import com.example.medication_demo.utils.parseMedicineDate
import com.example.medication_demo.reminder.scheduleMedicineDose
import com.example.medication_demo.reminder.findNextScheduledDose
import com.example.medication_demo.storage.CurrentUserStorage
import com.example.medication_demo.reminder.scheduleMedicineAlarm
import com.example.medication_demo.utils.parseMedicineTime
import com.example.medication_demo.reminder.cancelRefillReminder

class MedicineViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var currentUserId = "guest"
    private var localStorage =
        MedicineLocalStorage(
            context = application,
            userId = "guest"
        )
    private val medicineRepository = MedicineRepository()

    private val _medicines = MutableStateFlow(localStorage.loadMedicines())
    val medicines = _medicines.asStateFlow()

    private val _archivedMedicines = MutableStateFlow<List<ArchivedMedicine>>(localStorage.loadArchivedMedicines())
    val archivedMedicines: StateFlow<List<ArchivedMedicine>> = _archivedMedicines.asStateFlow()

    private val _medicineName = MutableStateFlow("")
    val medicineName: StateFlow<String> = _medicineName.asStateFlow()

    private val _remainingQuantities = MutableStateFlow<Map<Int, Double>>(localStorage.loadRemainingQuantities())
    val remainingQuantities: StateFlow<Map<Int, Double>> = _remainingQuantities.asStateFlow()

    private val _rescheduledDoses = MutableStateFlow<List<RescheduledDose>>(localStorage.loadRescheduledDoses())
    val rescheduledDoses: StateFlow<List<RescheduledDose>> = _rescheduledDoses.asStateFlow()

    private val _scheduleSnapshots = MutableStateFlow<List<MedicineScheduleSnapshot>>(localStorage.loadScheduleSnapshots())
    val scheduleSnapshots: StateFlow<List<MedicineScheduleSnapshot>> = _scheduleSnapshots.asStateFlow()

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity.asStateFlow()

    private val _dosageAmount = MutableStateFlow("")
    val dosageAmount: StateFlow<String> = _dosageAmount.asStateFlow()

    private val _dosageType = MutableStateFlow("Tablet")
    val dosageType: StateFlow<String> = _dosageType.asStateFlow()

    private val _refillQuantity = MutableStateFlow("")
    val refillQuantity: StateFlow<String> = _refillQuantity.asStateFlow()

    private val _frequency = MutableStateFlow("Once a day")
    val frequency: StateFlow<String> = _frequency.asStateFlow()

    private val _frequencyDraft = MutableStateFlow("Once a day")
    val frequencyDraft: StateFlow<String> = _frequencyDraft.asStateFlow()

    private val _repeatReminderEnabled = MutableStateFlow(false)
    val repeatReminderEnabled = _repeatReminderEnabled.asStateFlow()

    private val _repeatIntervalMinutes = MutableStateFlow("")
    val repeatIntervalMinutes = _repeatIntervalMinutes.asStateFlow()

    private val _repeatCount = MutableStateFlow(3)
    val repeatCount = _repeatCount.asStateFlow()

    // Is it using Custom Frequency
    private val _isCustomFrequency = MutableStateFlow(false)
    val isCustomFrequency: StateFlow<Boolean> = _isCustomFrequency.asStateFlow()

    // Custom: Every [2] [Days]
    private val _customFrequencyNumber = MutableStateFlow("")
    val customFrequencyNumber: StateFlow<String> = _customFrequencyNumber.asStateFlow()

    private val _customFrequencyUnit = MutableStateFlow("Days")
    val customFrequencyUnit: StateFlow<String> = _customFrequencyUnit.asStateFlow()

    private val _showFrequencyDialog = MutableStateFlow(false)
    val showFrequencyDialog: StateFlow<Boolean> = _showFrequencyDialog.asStateFlow()

    private val dateFormatter =
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy",
            Locale.ENGLISH
        )
    private val _startDate = MutableStateFlow(getMalaysiaDate().format(dateFormatter))
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _refillReminderEnabled = MutableStateFlow(false)
    val refillReminderEnabled: StateFlow<Boolean> = _refillReminderEnabled.asStateFlow()

    private val _reminderTimes = MutableStateFlow(listOf(ReminderTimeUi(time = "12:00 AM")))
    val reminderTimes: StateFlow<List<ReminderTimeUi>> = _reminderTimes.asStateFlow()

    private val _requiredReminderTimeCount = MutableStateFlow(1)
    val requiredReminderTimeCount: StateFlow<Int> = _requiredReminderTimeCount.asStateFlow()

    // Error message
    private val _medicineNameError = MutableStateFlow<String?>(null)
    val medicineNameError: StateFlow<String?> = _medicineNameError.asStateFlow()

    private val _quantityError = MutableStateFlow<String?>(null)
    val quantityError: StateFlow<String?> = _quantityError.asStateFlow()

    private val _dosageAmountError = MutableStateFlow<String?>(null)
    val dosageAmountError: StateFlow<String?> = _dosageAmountError.asStateFlow()

    private val _refillQuantityError = MutableStateFlow<String?>(null)
    val refillQuantityError: StateFlow<String?> = _refillQuantityError.asStateFlow()

    private val _customFrequencyError = MutableStateFlow(false)
    val customFrequencyError: StateFlow<Boolean> = _customFrequencyError.asStateFlow()

    private val _reminderTimeError = MutableStateFlow<String?>(null)
    val reminderTimeError: StateFlow<String?> = _reminderTimeError.asStateFlow()

    private val _repeatIntervalError = MutableStateFlow<String?>(null)
    val repeatIntervalError = _repeatIntervalError.asStateFlow()

    private val _presetImageRes = MutableStateFlow<Int?>(null)
    val presetImageRes: StateFlow<Int?> = _presetImageRes.asStateFlow()

    private val _galleryImageUri = MutableStateFlow<String?>(null)
    val galleryImageUri: StateFlow<String?> = _galleryImageUri.asStateFlow()

    private val _takenRecords = MutableStateFlow<List<MedicationTakenRecord>>(localStorage.loadTakenRecords())
    val takenRecords: StateFlow<List<MedicationTakenRecord>> = _takenRecords.asStateFlow()

    private val _missedRecords = MutableStateFlow<List<MedicationMissedRecord>>(localStorage.loadMissedRecords())

    val missedRecords: StateFlow<List<MedicationMissedRecord>> = _missedRecords.asStateFlow()

    private val _lowStockMedicineId = MutableStateFlow<Int?>(null)
    val lowStockMedicineId: StateFlow<Int?> = _lowStockMedicineId.asStateFlow()

    private val _insufficientStockMedicineId = MutableStateFlow<Int?>(null)
    val insufficientStockMedicineId: StateFlow<Int?> = _insufficientStockMedicineId.asStateFlow()
    private fun saveScheduleSnapshot(
        medicine: Medicine,
        effectiveDate: LocalDate
    ) {

        val snapshot =
            MedicineScheduleSnapshot(
                medicineId = medicine.id,
                effectiveDate = effectiveDate,
                quantity = medicine.quantity,
                dosageAmount = medicine.dosageAmount,
                dosageType = medicine.dosageType,
                frequency = medicine.frequency,
                reminderTimes = medicine.reminderTimes,
                startDate = medicine.startDate,
                name = medicine.name,

                presetImageRes = medicine.presetImageRes,
                galleryImageUri = medicine.galleryImageUri
            )

        _scheduleSnapshots.value =
            _scheduleSnapshots.value
                .filterNot { existing ->
                    existing.medicineId == medicine.id &&
                            existing.effectiveDate ==
                            effectiveDate
                } + snapshot
        localStorage.saveScheduleSnapshots(_scheduleSnapshots.value)
    }

    fun rescheduleDose(
        medicineId: Int,
        doseIndex: Int,
        originalTime: String,
        newTime: String
    ) {
        val today =
            getMalaysiaDate()

        val medicine =
            _medicines.value.find {
                it.id == medicineId
            } ?: return

        val newLocalTime =
            parseMedicineTime(
                newTime
            ) ?: return

        val newDateTime =
            today.atTime(
                newLocalTime
            )

        val nowDateTime =
            getMalaysiaDate()
                .atTime(
                    getMalaysiaTime()
                )

        // Do not schedule to a time that has already passed.
        if (
            !newDateTime.isAfter(
                nowDateTime
            )
        ) {
            return
        }

        // Save rescheduled dose for UI / History.
        _rescheduledDoses.value =
            _rescheduledDoses.value
                .filterNot { dose ->
                    dose.medicineId == medicineId &&
                            dose.date == today &&
                            dose.doseIndex == doseIndex
                } +
                    RescheduledDose(
                        medicineId = medicineId,
                        doseIndex = doseIndex,
                        date = today,
                        originalTime = originalTime,
                        newTime = newTime
                    )

        localStorage.saveRescheduledDoses(
            _rescheduledDoses.value
        )

        // Cancel the current original alarm and repeat reminders.
        cancelMedicineReminders(
            context = getApplication(),
            userId = currentUserId,
            medicineId = medicineId
        )

        // Schedule this dose at the new time.
        scheduleMedicineAlarm(
            context = getApplication(),
            userId = currentUserId,
            medicineId = medicine.id,
            medicineName = medicine.name,
            doseIndex = doseIndex,
            doseDate = today,
            scheduledTime = newTime,
            scheduledLocalTime = newLocalTime,
            repeatReminderEnabled =
                medicine.repeatReminderEnabled,
            repeatIntervalMinutes =
                medicine.repeatIntervalMinutes,
            repeatCount =
                medicine.repeatCount
        )
    }

    fun markDoseAsTaken(
        medicineId: Int,
        doseIndex: Int,
        reminderTime: String
    ) {
        val today = getMalaysiaDate()
        val medicine =
            _medicines.value.find {
                it.id == medicineId
            } ?: return
        val actualTakenTime =
            getMalaysiaTime().format(
                DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    Locale.ENGLISH
                )
            )
        val alreadyTaken =
            _takenRecords.value.any { record ->
                record.medicineId == medicineId &&
                        record.date == today &&
                        record.doseIndex == doseIndex
            }
        if (!alreadyTaken) {
            _takenRecords.value +=
                MedicationTakenRecord(
                    medicineId = medicineId,
                    date = today,
                    doseIndex = doseIndex,
                    reminderTime = reminderTime,
                    takenTime = actualTakenTime,
                    // Snapshot
                    dosageAmount = medicine.dosageAmount,
                    dosageType = medicine.dosageType
                )
            localStorage.saveTakenRecords(_takenRecords.value)
            reduceRemainingQuantity(medicineId = medicineId)
        }
    }

    fun markDoseAsMissed(
        medicineId: Int,
        doseIndex: Int,
        reminderTime: String
    ) {
        val today = getMalaysiaDate()

        val medicine = _medicines.value.firstOrNull {
            it.id == medicineId
        } ?: return

        val alreadyTaken = _takenRecords.value.any {
            it.medicineId == medicineId &&
                    it.date == today &&
                    it.doseIndex == doseIndex
        }

        val alreadyMissed = _missedRecords.value.any {
            it.medicineId == medicineId &&
                    it.date == today &&
                    it.doseIndex == doseIndex
        }

        if (alreadyTaken || alreadyMissed) {
            return
        }

        _missedRecords.value += MedicationMissedRecord(
            medicineId = medicineId,
            date = today,
            doseIndex = doseIndex,
            reminderTime = reminderTime,
            dosageAmount = medicine.dosageAmount,
            dosageType = medicine.dosageType
        )

        localStorage.saveMissedRecords(
            _missedRecords.value
        )
    }

    fun getRemainingQuantity(
        medicine: Medicine
    ): Double {

        return _remainingQuantities.value[
            medicine.id
        ] ?: medicine.quantity.toDoubleOrNull() ?: 0.0
    }

    fun getRemainingQuantityText(
        medicine: Medicine
    ): String {

        val remaining =
            getRemainingQuantity(medicine)

        return if (remaining % 1.0 == 0.0) {
            remaining.toInt().toString()
        } else {
            remaining.toString()
        }
    }

    private fun reduceRemainingQuantity(
        medicineId: Int
    ) {

        val medicine =
            _medicines.value.find {
                it.id == medicineId
            } ?: return

        val currentRemaining =
            getRemainingQuantity(medicine)

        val dosage =
            medicine.dosageAmount
                .toDoubleOrNull()
                ?: return

        if (dosage <= 0) {
            return
        }

        val newRemaining =
            (currentRemaining - dosage)
                .coerceAtLeast(0.0)
        _remainingQuantities.value += (medicineId to newRemaining)
        localStorage.saveRemainingQuantities(_remainingQuantities.value)

        if (
            newRemaining < dosage
        ) {
            _insufficientStockMedicineId.value =
                medicine.id
        }
        checkRefillReminder(
            medicine = medicine,
            previousRemaining = currentRemaining,
            newRemaining = newRemaining
        )
    }

    private fun checkRefillReminder(
        medicine: Medicine,
        previousRemaining: Double,
        newRemaining: Double
    ) {
        if (!medicine.refillReminderEnabled) {
            return
        }
        val threshold =
            medicine.refillQuantity
                .toDoubleOrNull()
                ?: return
        if (previousRemaining > threshold && newRemaining <= threshold) {
            _lowStockMedicineId.value = medicine.id
        }
    }

    fun clearInsufficientStockEvent() {
        _insufficientStockMedicineId.value = null
    }

    fun clearLowStockEvent() {
        _lowStockMedicineId.value = null
    }

    fun markAsNeededMedicineTaken(
        medicineId: Int
    ): Boolean {
        val today = getMalaysiaDate()
        val currentTime = getMalaysiaTime()
        val timeFormatter =
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ENGLISH
            )
        val takenTime = currentTime.format(timeFormatter)
        val alreadyTakenThisMinute =
            _takenRecords.value.any { record ->
                record.medicineId == medicineId &&
                        record.date == today &&
                        record.reminderTime == takenTime
            }
        if (alreadyTakenThisMinute) {
            return false
        }
        val nextDoseIndex =
            _takenRecords.value
                .filter { record ->
                    record.medicineId == medicineId &&
                            record.date == today
                }
                .maxOfOrNull { record ->
                    record.doseIndex
                }
                ?.plus(1)
                ?: 0

        val medicine =
            _medicines.value.find {
                it.id == medicineId
            } ?: return false

        val newRecord =
            MedicationTakenRecord(
                medicineId = medicineId,
                date = today,
                doseIndex = nextDoseIndex,
                reminderTime = takenTime,
                takenTime = takenTime,
                dosageAmount = medicine.dosageAmount,
                dosageType = medicine.dosageType
            )

        _takenRecords.value += newRecord
        localStorage.saveTakenRecords(_takenRecords.value)
        reduceRemainingQuantity(medicineId = medicineId)
        return true
    }

    fun onRefillReminderEnabledChange(enabled: Boolean) {
        _refillReminderEnabled.value = enabled
        if (!enabled) {
            _refillQuantityError.value = null
        }
    }

    fun onMedicineNameChange(value: String) {
        _medicineName.value = value
        if (value.isNotBlank()) {
            _medicineNameError.value = null
        }
    }

    fun onQuantityChange(value: String) {
        _quantity.value = value
        if (value.isNotBlank()) {
            _quantityError.value = null
        }
    }

    fun onDosageAmountChange(value: String) {
        _dosageAmount.value = value
        if (value.isNotBlank()) {
            _dosageAmountError.value = null
        }
    }

    fun onDosageTypeChange(value: String) {
        _dosageType.value = value
    }

    fun onRefillQuantityChange(value: String) {
        _refillQuantity.value = value
        val refillValue = value.toIntOrNull()
        val quantityValue = _quantity.value.toIntOrNull()
        val isValid = value.isNotBlank() && refillValue != null && refillValue > 0 && (
                quantityValue == null || quantityValue <= 0 || refillValue < quantityValue
                )
        if (isValid) {
            _refillQuantityError.value = null
        }
    }

    fun openFrequencyEditor() {
        _frequencyDraft.value = _frequency.value
        if (_frequency.value.startsWith("Every ")) {
            _isCustomFrequency.value = true
            val parts = _frequency.value.split(" ")
            if (parts.size >= 3) {
                _customFrequencyNumber.value = parts[1]
                _customFrequencyUnit.value =
                    when (parts[2].lowercase()) {
                        "hour", "hours" -> "Hours"
                        "day", "days" -> "Days"
                        "week", "weeks" -> "Weeks"
                        "month", "months" -> "Months"
                        else -> "Days"
                    }
            }
        } else {
            _isCustomFrequency.value = false
            _customFrequencyNumber.value = ""
            _customFrequencyError.value = false
        }
    }

    fun selectFrequencyOption(
        value: String
    ) {
        _frequencyDraft.value = value
        _isCustomFrequency.value = false
    }

    fun selectCustomFrequency() {
        _isCustomFrequency.value = true
        _customFrequencyError.value = false
    }

    fun onCustomFrequencyNumberChange(
        value: String
    ) {
        if (value.all { it.isDigit() }) {
            _customFrequencyNumber.value = value
            if (value.isNotBlank() && (value.toIntOrNull() ?: 0) > 0) {
                _customFrequencyError.value = false
            }
        }
    }

    fun confirmFrequency(): Boolean {
        if (_isCustomFrequency.value) {
            val number = _customFrequencyNumber.value
            val  numberValue =number.toIntOrNull()
            if (number.isBlank() || numberValue == null || numberValue <= 0) {
                _customFrequencyError.value = true
                return false
            }
            _customFrequencyError.value = false

            val unit = if (number == "1") {
                when (_customFrequencyUnit.value) {
                    "Hours" -> "hour"
                    "Days" -> "day"
                    "Weeks" -> "week"
                    "Months" -> "month"
                    else -> "day"
                }
            } else
                _customFrequencyUnit.value.lowercase()

            _frequency.value = "Every $numberValue $unit"
        } else{
            _frequency.value = _frequencyDraft.value
            _customFrequencyError.value = false
        }
        _requiredReminderTimeCount.value = getRequiredTimeCount(_frequency.value)
        adjustReminderTimesForFrequency()
        return true
    }

    private fun adjustReminderTimesForFrequency() {
        val required = _requiredReminderTimeCount.value
        if (required == 0) {
            _reminderTimes.value = emptyList()
            return
        }
        val current =
            _reminderTimes.value
                .take(required)
                .toMutableList()
        while (current.size < required) {
            current.add(
                ReminderTimeUi(
                    time = "12:00 AM"
                )
            )
        }
        _reminderTimes.value = current
    }

    fun onCustomFrequencyUnitChange(
        value: String
    ) { _customFrequencyUnit.value = value }

    fun openFrequencyDialog() {
        openFrequencyEditor()
        _showFrequencyDialog.value = true
    }

    fun closeFrequencyDialog() { _showFrequencyDialog.value = false }

    fun onStartDateChange(value: String) {
        try {
            LocalDate.parse(
                value,
                dateFormatter
            )
        } catch (_: Exception) {
            return
        }
        _startDate.value = value
    }

    fun onNotesChange(value: String) {
        _notes.value = value
    }

    private fun validateMedicineForm(
        editingMedicineId: Int? = null)
    : Boolean {
        var hasError = false
        _medicineNameError.value =
            when {
                _medicineName.value.isBlank() ->
                    "Please enter the medicine name."
                _medicines.value.any { medicine ->
                    medicine.id != editingMedicineId &&
                            medicine.name.trim().equals(
                                _medicineName.value.trim(),
                                ignoreCase = true
                            )
                } ->
                    "This medicine already exists."
                else ->
                    null
            }

        if (_medicineNameError.value != null) {
            hasError = true
        }
        val quantityValue = _quantity.value.toIntOrNull()

        _quantityError.value = when {
            _quantity.value.isBlank() ->
                "Please enter the quantity."

            quantityValue == null || quantityValue <= 0 ->
                "Quantity must be greater than 0."

            else -> null
        }

        if (_quantityError.value != null) {
            hasError = true
        }

        val dosageValue = _dosageAmount.value.toDoubleOrNull()

        _dosageAmountError.value = when {

            _dosageAmount.value.isBlank() ->
                "Please enter the dosage amount."

            dosageValue == null || dosageValue <= 0.0 ->
                "Dosage amount must be greater than 0."

            quantityValue != null &&
                    quantityValue > 0 &&
                    dosageValue > quantityValue ->
                "Dosage amount cannot exceed the medicine quantity."

            else -> null
        }

        if (_dosageAmountError.value != null) {
            hasError = true
        }

        if (_refillReminderEnabled.value) {
            val refillValue = _refillQuantity.value.toIntOrNull()

            _refillQuantityError.value = when {
                _refillQuantity.value.isBlank() ->
                    "Please enter the refill reminder quantity."

                refillValue == null || refillValue <= 0 ->
                    "Refill reminder quantity must be greater than 0."

                quantityValue != null &&
                        quantityValue > 0 &&
                        refillValue >= quantityValue ->
                    "Refill reminder quantity must be less than the medicine quantity."

                else -> null
            }
        } else {
            _refillQuantityError.value = null
        }

        if (_refillQuantityError.value != null) {
            hasError = true
        }

        if (_isCustomFrequency.value) {
            val customFrequencyValue =
                _customFrequencyNumber.value.toIntOrNull()

            if (
                _customFrequencyNumber.value.isBlank() ||
                customFrequencyValue == null ||
                customFrequencyValue <= 0
            ) {
                _customFrequencyError.value = true
                hasError = true
            } else {
                _customFrequencyError.value = false
            }
        } else {
            _customFrequencyError.value = false
        }

        if (!validateReminderTimes()) {
            hasError = true
        }

        if (_repeatReminderEnabled.value) {
            val interval = _repeatIntervalMinutes.value.toIntOrNull()
            _repeatIntervalError.value =
                when {
                    _repeatIntervalMinutes.value.isBlank() ->
                        "Please enter the repeat interval."

                    interval == null || interval <= 0 ->
                        "Repeat interval must be at least 1 minute."

                    interval > 120 ->
                        "Repeat interval cannot exceed 120 minutes."

                    else ->
                        null
                }

        } else {
            _repeatIntervalError.value = null
        }

        if (_repeatIntervalError.value != null) {
            hasError = true
        }

        return !hasError
    }

    fun refillMedicine(
        medicineId: Int,
        refillQuantity: Int
    ) {
        if (refillQuantity <= 0) {
            return
        }

        val medicine =
            _medicines.value.find {
                it.id == medicineId
            } ?: return

        val currentRemaining = getRemainingQuantity(medicine)
        val newRemaining = currentRemaining + refillQuantity.toDouble()

        _remainingQuantities.value += (medicineId to newRemaining)
        localStorage.saveRemainingQuantities(_remainingQuantities.value)
    }

    fun updateMedicine(id: Int): Boolean {
        if (!validateMedicineForm(editingMedicineId = id)
        ) {
            return false
        }
        val oldMedicine =
            _medicines.value.find {
                it.id == id
            } ?: return false
        // Original total quantity stored in Medicine
        val oldTotalQuantity = oldMedicine.quantity.toDoubleOrNull() ?: 0.0
        // Current actual remaining quantity
        val oldRemaining = getRemainingQuantity(oldMedicine)
        // Quantity entered by user in Edit Medicine
        val editedQuantity = _quantity.value.toDoubleOrNull() ?: return false

        val quantityDifference = editedQuantity - oldRemaining
        val newTotalQuantity =
            (oldTotalQuantity + quantityDifference)
                .coerceAtLeast(0.0)
        val newRemaining = editedQuantity.coerceAtLeast(0.0)
        val newTotalQuantityText =
            if (newTotalQuantity % 1.0 == 0.0) {
                newTotalQuantity.toInt().toString()
            } else {
                newTotalQuantity.toString()
            }
        _medicines.value =
            _medicines.value.map { medicine ->
                if (medicine.id == id) {
                    medicine.copy(
                        name = _medicineName.value,
                        quantity = newTotalQuantityText,
                        dosageAmount = _dosageAmount.value,
                        dosageType = _dosageType.value,
                        refillReminderEnabled = _refillReminderEnabled.value,
                        refillQuantity = _refillQuantity.value,
                        frequency = _frequency.value,
                        reminderTimes = _reminderTimes.value,
                        startDate = _startDate.value,
                        notes = _notes.value,
                        presetImageRes = _presetImageRes.value,
                        galleryImageUri = _galleryImageUri.value,
                        repeatReminderEnabled = _repeatReminderEnabled.value,
                        repeatIntervalMinutes = _repeatIntervalMinutes.value.toIntOrNull() ?: medicine.repeatIntervalMinutes,
                        repeatCount = _repeatCount.value,
                    )
                } else {
                    medicine
                }
            }
        localStorage.saveMedicines(_medicines.value)
        _remainingQuantities.value += (id to newRemaining)
        localStorage.saveRemainingQuantities(_remainingQuantities.value)
        val updatedMedicine =
            _medicines.value.find {
                it.id == id
            }

        if (updatedMedicine != null) {
            saveScheduleSnapshot(
                medicine = updatedMedicine,
                effectiveDate = getMalaysiaDate()
            )
            cancelMedicineReminders(
                context = getApplication(),
                userId = currentUserId,
                medicineId = updatedMedicine.id
            )
            scheduleNextMedicineDose(
                updatedMedicine
            )
            viewModelScope.launch {
                medicineRepository.updateMedicine(
                    updatedMedicine
                )
            }
        }

        return true
    }

    fun getScheduleSnapshotForDate(
        medicineId: Int,
        date: LocalDate
    ): MedicineScheduleSnapshot? {

        return _scheduleSnapshots.value
            .filter { snapshot ->
                snapshot.medicineId == medicineId &&
                        !snapshot.effectiveDate
                            .isAfter(date)
            }
            .maxByOrNull {
                it.effectiveDate
            }
    }

    fun getMedicineForHistoricalDate(
        medicine: Medicine,
        date: LocalDate
    ): Medicine {

        val snapshot =
            getScheduleSnapshotForDate(
                medicineId = medicine.id,
                date = date
            ) ?: return medicine

        return medicine.copy(
            quantity = snapshot.quantity,
            dosageAmount = snapshot.dosageAmount,
            dosageType = snapshot.dosageType,
            frequency = snapshot.frequency,
            reminderTimes = snapshot.reminderTimes,
            startDate = snapshot.startDate
        )
    }

    fun addMedicine(): Boolean {

        if (!validateMedicineForm()) {
            return false
        }

        val newMedicine = Medicine(
            id = generateNextMedicineId(),
            name = _medicineName.value,
            quantity = _quantity.value,
            dosageAmount = _dosageAmount.value,
            dosageType = _dosageType.value,
            refillReminderEnabled = _refillReminderEnabled.value,
            refillQuantity = _refillQuantity.value,
            frequency = _frequency.value,
            reminderTimes = _reminderTimes.value,
            startDate = _startDate.value,
            notes = _notes.value,
            presetImageRes = _presetImageRes.value,
            galleryImageUri = _galleryImageUri.value,
            repeatReminderEnabled = _repeatReminderEnabled.value,
            repeatIntervalMinutes = _repeatIntervalMinutes.value.toIntOrNull() ?: 15,
            repeatCount = _repeatCount.value
        )

        _medicines.value += newMedicine
        localStorage.saveMedicines(_medicines.value)
        _remainingQuantities.value += (newMedicine.id to (newMedicine.quantity.toDoubleOrNull() ?: 0.0))
        localStorage.saveRemainingQuantities(_remainingQuantities.value)
        viewModelScope.launch {
            medicineRepository.addMedicine(
                newMedicine
            )
        }
        val medicineStartDate =
            parseMedicineDate(
                newMedicine.startDate
            ) ?: getMalaysiaDate()
        saveScheduleSnapshot(
            medicine = newMedicine,
            effectiveDate = medicineStartDate
        )
        scheduleNextMedicineDose(newMedicine)
        return true
    }

    fun deleteMedicine(id: Int) {

        val medicineToDelete =
            _medicines.value.find { medicine ->
                medicine.id == id
            } ?: return

        // Cancel all pending medicine reminders first
        cancelMedicineReminders(
            context = getApplication(),
            userId = currentUserId,
            medicineId = id
        )
        cancelRefillReminder(
            context = getApplication(),
            medicineId = id
        )

        // Keep a copy for History
        val archivedMedicine =
            ArchivedMedicine(
                medicine = medicineToDelete,
                deletedDate = getMalaysiaDate(),
                deletedTime = getMalaysiaTime()
            )

        _archivedMedicines.value =
            _archivedMedicines.value
                .filterNot { archived ->
                    archived.medicine.id == id
                } + archivedMedicine

        localStorage.saveArchivedMedicines(
            _archivedMedicines.value
        )

        // Remove from current active medicine list
        _medicines.value =
            _medicines.value.filter { medicine ->
                medicine.id != id
            }

        localStorage.saveMedicines(
            _medicines.value
        )

        // Current stock is no longer needed
        _remainingQuantities.value -= id

        localStorage.saveRemainingQuantities(
            _remainingQuantities.value
        )

        viewModelScope.launch {
            medicineRepository.deleteMedicine(id)
        }

        // Clear low stock event if this medicine
        // happens to be the current event
        if (_lowStockMedicineId.value == id) {
            _lowStockMedicineId.value = null
        }
    }

    private fun scheduleNextMedicineDose(
        medicine: Medicine
    ) {
        if (!medicine.reminderEnabled) {
            return
        }

        val nowDateTime =
            getMalaysiaDate()
                .atTime(
                    getMalaysiaTime()
                )

        val nextDose =
            findNextScheduledDose(
                medicine = medicine,
                afterDateTime = nowDateTime.minusSeconds(1)
            ) ?: return
        val scheduledDateTime =
            nextDose.doseDate
                .atTime(
                    nextDose.scheduledLocalTime
                )

        val delayMillis =
            java.time.Duration
                .between(
                    nowDateTime,
                    scheduledDateTime
                )
                .toMillis()
                .coerceAtLeast(0L)

        scheduleMedicineDose(
            context = getApplication(),
            userId = currentUserId,
            medicine = medicine,
            doseIndex = nextDose.doseIndex,
            doseDate = nextDose.doseDate,
            scheduledTime = nextDose.scheduledTime,
            delayMillis = delayMillis
        )
    }

    fun addReminderTime() {
        if (_requiredReminderTimeCount.value == 0) {
            return
        }
        _reminderTimes.value += ReminderTimeUi(
            time = "12:00 AM"
        )
        _reminderTimeError.value = null
    }

    private fun validateReminderTimes(): Boolean {

        var isValid = true

        _reminderTimeError.value = null

        // Number of reminder times
        val required =
            _requiredReminderTimeCount.value

        if (required == 0) {
            return true
        }

        val actual =
            _reminderTimes.value.size

        if (actual != required) {

            _reminderTimeError.value =
                when (_frequency.value) {

                    "Once a day" ->
                        "Once a day requires exactly 1 reminder time."

                    "Twice a day" ->
                        "Twice a day requires exactly 2 reminder times."

                    "3 times a day" ->
                        "3 times a day requires exactly 3 reminder times."

                    "Once a week" ->
                        "Once a week requires exactly 1 reminder time."

                    else ->
                        "${_frequency.value} requires exactly 1 reminder time."
                }

            isValid = false
        }

        // Duplicate time
        val times =
            _reminderTimes.value.map {
                it.time
            }

        if (
            times.size !=
            times.distinct().size
        ) {
            _reminderTimeError.value =
                "Reminder times cannot be the same."

            isValid = false
        }

        return isValid
    }
    fun removeReminderTime(index: Int) {
        if (_reminderTimes.value.size <= 1) {
            return
        }
        _reminderTimes.value = _reminderTimes.value.filterIndexed { currentIndex, _ ->
            currentIndex != index
        }
        _reminderTimeError.value = null
    }

    fun updateReminderTime(
        index: Int,
        newTime: String
    ) {
        val current = _reminderTimes.value.toMutableList()
        current[index] = current[index].copy(
            time = newTime
        )
        _reminderTimes.value = current
        _reminderTimeError.value = null
    }

    fun updateReminderEnabled(
        id: Int,
        enabled: Boolean
    ) {
        _medicines.value = _medicines.value.map { medicine ->
            if (medicine.id == id) {
                medicine.copy(
                    reminderEnabled = enabled
                )
            } else {
                medicine
            }
        }
        localStorage.saveMedicines(_medicines.value)
    }

    private fun getRequiredTimeCount(
        frequency: String
    ): Int {
        return when (frequency) {
            "Once a day" -> 1
            "Twice a day" -> 2
            "3 times a day" -> 3
            "Once a week" -> 1
            "As needed" -> 0
            else -> {
                if (frequency.startsWith("Every ")) {
                    1
                } else {
                    1
                }
            }
        }
    }

    fun loadMedicineForEdit(medicine: Medicine) {
        _medicineName.value = medicine.name
        _quantity.value = getRemainingQuantityText(medicine)
        _dosageAmount.value = medicine.dosageAmount
        _dosageType.value = medicine.dosageType
        _refillReminderEnabled.value = medicine.refillReminderEnabled
        _refillQuantity.value = medicine.refillQuantity
        _frequency.value = medicine.frequency
        _frequencyDraft.value = medicine.frequency
        _reminderTimes.value = medicine.reminderTimes
        _startDate.value = medicine.startDate
        _notes.value = medicine.notes
        _presetImageRes.value = medicine.presetImageRes
        _galleryImageUri.value = medicine.galleryImageUri
        _repeatReminderEnabled.value = medicine.repeatReminderEnabled
        _repeatIntervalMinutes.value =
            if (medicine.repeatReminderEnabled) {
                medicine.repeatIntervalMinutes.toString()
            } else {
                ""
            }
        _repeatCount.value = medicine.repeatCount

        _requiredReminderTimeCount.value =
            getRequiredTimeCount(medicine.frequency)

        if (medicine.frequency.startsWith("Every ")) {
            _isCustomFrequency.value = true

            val parts = medicine.frequency.split(" ")

            if (parts.size >= 3) {
                _customFrequencyNumber.value = parts[1]

                _customFrequencyUnit.value =
                    when (parts[2].lowercase()) {
                        "hour", "hours" -> "Hours"
                        "day", "days" -> "Days"
                        "week", "weeks" -> "Weeks"
                        "month", "months" -> "Months"
                        else -> "Days"
                    }
            }
        } else {
            _isCustomFrequency.value = false
            _customFrequencyNumber.value = ""
            _customFrequencyUnit.value = "Days"
        }

        // Clear old errors
        _medicineNameError.value = null
        _quantityError.value = null
        _dosageAmountError.value = null
        _refillQuantityError.value = null
        _customFrequencyError.value = false
        _reminderTimeError.value = null
    }

    fun resetAddMedicineForm() {
        _medicineName.value = ""
        _quantity.value = ""
        _dosageAmount.value = ""
        _dosageType.value = "Tablet"
        _refillReminderEnabled.value = false
        _refillQuantity.value = ""
        _frequency.value = "Once a day"
        _frequencyDraft.value = "Once a day"
        _isCustomFrequency.value = false
        _customFrequencyNumber.value = ""
        _customFrequencyUnit.value = "Days"
        _customFrequencyError.value = false
        _requiredReminderTimeCount.value = 1
        _reminderTimes.value = listOf(
            ReminderTimeUi(
                time = "12:00 AM"
            )
        )
        _startDate.value = getMalaysiaDate().format(dateFormatter)
        _notes.value = ""
        _presetImageRes.value = null
        _galleryImageUri.value = null
        _repeatReminderEnabled.value = false
        _repeatIntervalMinutes.value = ""
        _repeatCount.value = 3

        // Clear field errors
        _medicineNameError.value = null
        _quantityError.value = null
        _dosageAmountError.value = null
        _refillQuantityError.value = null
        _reminderTimeError.value = null
        _repeatIntervalError.value = null
    }

    fun onPresetImageSelected(imageRes: Int) {
        _presetImageRes.value = imageRes
        // Choose Preset then cancel gallery image
        _galleryImageUri.value = null
    }

    fun onGalleryImageSelected(uri: String) {
        _galleryImageUri.value = uri

        // Choose Gallery then cancel preset
        _presetImageRes.value = null
    }

    private fun generateNextMedicineId(): Int {

        val activeIds =
            _medicines.value.map {
                it.id
            }
        val archivedIds =
            _archivedMedicines.value.map {
                it.medicine.id
            }
        val takenRecordIds =
            _takenRecords.value.map {
                it.medicineId
            }
        val rescheduledDoseIds =
            _rescheduledDoses.value.map {
                it.medicineId
            }

        val snapshotIds =
            _scheduleSnapshots.value.map {
                it.medicineId
            }
        val highestId =
            (
                    activeIds +
                            archivedIds +
                            takenRecordIds +
                            rescheduledDoseIds +
                            snapshotIds
                    )
                .maxOrNull() ?: 0
        return highestId + 1
    }

    fun switchUser(userId: String) {
        CurrentUserStorage.saveUserId(
            context = getApplication(),
            userId = userId
        )
        currentUserId = userId
        localStorage =
            MedicineLocalStorage(
                context = getApplication(),
                userId = userId
            )
        _medicines.value = localStorage.loadMedicines()
        _archivedMedicines.value = localStorage.loadArchivedMedicines()
        _remainingQuantities.value = localStorage.loadRemainingQuantities()
        _rescheduledDoses.value = localStorage.loadRescheduledDoses()
        _scheduleSnapshots.value = localStorage.loadScheduleSnapshots()
        _takenRecords.value = localStorage.loadTakenRecords()
        _missedRecords.value = localStorage.loadMissedRecords()
        _lowStockMedicineId.value = null
        resetAddMedicineForm()
        _medicines.value.forEach { medicine ->
            scheduleNextMedicineDose(
                medicine
            )
        }
        syncMedicinesFromCloud()
    }

    fun syncMedicinesFromCloud() {
        viewModelScope.launch {
            try {
                val cloudMedicines =
                    medicineRepository.getMedicines()

                // Keep the old local medicines first,
                // so their existing reminders can be canceled.
                val oldMedicines =
                    _medicines.value

                val uniqueCloudMedicines =
                    cloudMedicines.distinctBy {
                        it.id
                    }

                // Cancel reminders based on the old local data.
                oldMedicines.forEach { medicine ->

                    cancelMedicineReminders(
                        context = getApplication(),
                        userId = currentUserId,
                        medicineId = medicine.id
                    )
                }

                // Replace local medicines with latest cloud data.
                _medicines.value = uniqueCloudMedicines
                localStorage.saveMedicines(_medicines.value
                )

                // If cloud medicine is first time on this phone,
                // initialize its local remaining quantity.
                val validMedicineIds =
                    uniqueCloudMedicines
                        .map { it.id }
                        .toSet()

                val updatedQuantities =
                    _remainingQuantities.value
                        .filterKeys { medicineId ->
                            medicineId in validMedicineIds
                        }
                        .toMutableMap()

                uniqueCloudMedicines.forEach { medicine ->

                    if (
                        !updatedQuantities.containsKey(
                            medicine.id
                        )
                    ) {
                        updatedQuantities[
                            medicine.id
                        ] =
                            medicine.quantity
                                .toDoubleOrNull()
                                ?: 0.0
                    }
                }

                _remainingQuantities.value = updatedQuantities
                localStorage.saveRemainingQuantities(_remainingQuantities.value
                )

                // Schedule reminders again using
                // the latest medicine data from cloud.
                _medicines.value.forEach { medicine ->

                    scheduleNextMedicineDose(
                        medicine
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "MedicineViewModel",
                    "Failed to sync medicines from Supabase",
                    e
                )
            }
        }
    }
    fun onRepeatReminderEnabledChange(
        enabled: Boolean
    ) {
        _repeatReminderEnabled.value = enabled
    }

    fun onRepeatIntervalChange(
        value: String
    ) {
        if (
            value.isEmpty() ||
            value.all { it.isDigit() }
        ) {
            _repeatIntervalMinutes.value = value
            _repeatIntervalError.value = null
        }
    }

    fun onRepeatCountChange(
        count: Int
    ) {
        _repeatCount.value = count
    }

    fun prepareForLogout() {

        val userId =
            CurrentUserStorage
                .getUserId(
                    getApplication()
                )
                ?: return

        _medicines.value.forEach { medicine ->

            cancelMedicineReminders(
                context = getApplication(),
                userId = userId,
                medicineId = medicine.id
            )
        }

        CurrentUserStorage.clearUserId(
            context = getApplication()
        )

        _medicines.value = emptyList()
        _archivedMedicines.value = emptyList()
        _remainingQuantities.value = emptyMap()
        _takenRecords.value = emptyList()
        _rescheduledDoses.value = emptyList()
        _scheduleSnapshots.value = emptyList()
        _lowStockMedicineId.value = null
        _insufficientStockMedicineId.value = null

        currentUserId = "guest"
    }
}