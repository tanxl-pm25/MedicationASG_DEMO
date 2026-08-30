package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.medication_demo.model.ReminderTimeUi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.utils.getMalaysiaDate

class MedicineViewModel : ViewModel() {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    private val _medicineName = MutableStateFlow("")
    val medicineName: StateFlow<String> = _medicineName.asStateFlow()

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

    private val _reminderTimes =
        MutableStateFlow(
            listOf(
                ReminderTimeUi(
                    time = "12:00 AM",
                    minutes = ""
                )
            )
        )
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

    private val _presetImageRes = MutableStateFlow<Int?>(null)
    val presetImageRes: StateFlow<Int?> = _presetImageRes.asStateFlow()

    private val _galleryImageUri = MutableStateFlow<String?>(null)
    val galleryImageUri: StateFlow<String?> = _galleryImageUri.asStateFlow()

    private val _takenRecords = MutableStateFlow<List<MedicationTakenRecord>>(emptyList())
    val takenRecords: StateFlow<List<MedicationTakenRecord>> = _takenRecords.asStateFlow()

    private val _rescheduledDoses = MutableStateFlow<List<RescheduledDose>>(emptyList())
    val rescheduledDoses: StateFlow<List<RescheduledDose>> = _rescheduledDoses.asStateFlow()

    fun rescheduleDose(
        medicineId: Int,
        originalTime: String,
        newTime: String
    ) {
        val today = getMalaysiaDate()
        _rescheduledDoses.value =
            _rescheduledDoses.value
                .filterNot { dose ->
                    dose.medicineId == medicineId &&
                            dose.date == today &&
                            dose.originalTime == originalTime
                } +
                    RescheduledDose(
                        medicineId = medicineId,
                        date = today,
                        originalTime = originalTime,
                        newTime = newTime
                    )
    }

    fun markDoseAsTaken(
        medicineId: Int,
        reminderTime: String
    ) {
        val record = MedicationTakenRecord(
            medicineId = medicineId,
            date = getMalaysiaDate(),
            reminderTime = reminderTime
        )

        val alreadyTaken =
            _takenRecords.value.any {
                it.medicineId == medicineId &&
                        it.date == record.date &&
                        it.reminderTime == reminderTime
            }

        if (!alreadyTaken) {
            _takenRecords.value += record
        }
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

    fun onFrequencyChange(value: String) {
        _frequency.value = value
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
        // As needed
        if (required == 0) {
            _reminderTimes.value = emptyList()
            return
        }
        val current = _reminderTimes.value.toMutableList()
        // If time not enough, automatically add on
        while (current.size < required) {
            current.add(
                ReminderTimeUi(
                    time = "12:00 AM",
                    minutes = "5"
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

    private fun validateMedicineForm(): Boolean {
        var hasError = false

        _medicineNameError.value =
            if (_medicineName.value.isBlank()) {
                "Please enter the medicine name."
            } else {
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

        return !hasError
    }

    fun updateMedicine(id: Int): Boolean {

        if (!validateMedicineForm()) {
            return false
        }

        _medicines.value =
            _medicines.value.map { medicine ->

                if (medicine.id == id) {
                    medicine.copy(
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
                        galleryImageUri = _galleryImageUri.value                    )
                } else {
                    medicine
                }
            }

        return true
    }
    fun addMedicine(): Boolean {

        if (!validateMedicineForm()) {
            return false
        }

        val newMedicine = Medicine(
            id = (_medicines.value.maxOfOrNull { it.id } ?: 0) + 1,
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
            galleryImageUri = _galleryImageUri.value
        )

        _medicines.value += newMedicine

        return true
    }

    fun deleteMedicine(id: Int) {
        _medicines.value = _medicines.value.filter { it.id != id }
    }

    fun addReminderTime() {
        if (_requiredReminderTimeCount.value == 0) {
            return
        }
        _reminderTimes.value += ReminderTimeUi(
            time = "12:00 AM",
            minutes = ""
        )
        _reminderTimeError.value = null
    }

    private fun validateReminderTimes(): Boolean {

        var isValid = true

        _reminderTimeError.value = null


        // ==============================
        // Reminder interval
        // ==============================

        _reminderTimes.value = _reminderTimes.value.map { reminder ->
            if (reminder.reminderOptionsEnabled) {
                val minutesValue = reminder.minutes.toIntOrNull()
                val error = when {
                    reminder.minutes.isBlank() -> "Please enter the reminder interval."
                    minutesValue == null || minutesValue <= 0 -> "Reminder interval must be greater than 0."
                    else -> null
                }
                if (error != null) {
                    isValid = false
                }
                reminder.copy(minutesError = error)
            } else {
                reminder.copy(minutesError = null)
            }
        }

        // Number of reminder times
        val required = _requiredReminderTimeCount.value
        if (required == 0) {
            return isValid
        }
        val actual = _reminderTimes.value.size
        if (actual != required) {
            _reminderTimeError.value = when (_frequency.value) {
                "Once a day" ->
                    "Once a day requires exactly 1 reminder time."
                "Twice a day" ->
                    "Twice a day requires exactly 2 reminder times."
                "3 times a day" ->
                    "3 times a day requires exactly 3 reminder times."
                "Once a week" ->
                    "Once a week requires exactly 1 reminder time."
                else -> "${_frequency.value} requires exactly 1 reminder time."
            }
            isValid = false
        }

        // Duplicate time
        val times = _reminderTimes.value.map { it.time }
        if (times.size != times.distinct().size) {
            _reminderTimeError.value = "Reminder times cannot be the same."
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

    fun updateReminderMinutes(
        index: Int,
        newMinutes: String
    ) {
        _reminderTimes.value =
            _reminderTimes.value.mapIndexed { currentIndex, reminder ->
                if (currentIndex == index) {
                    val minutesValue = newMinutes.toIntOrNull()
                    val isValid = newMinutes.isNotBlank() && minutesValue != null && minutesValue > 0
                    reminder.copy(
                        minutes = newMinutes,
                        minutesError =
                            if (isValid) {
                                null
                            } else reminder.minutesError
                    )
                } else reminder
            }
    }

    fun toggleReminderOptions(index: Int) {
        _reminderTimes.value = _reminderTimes.value.mapIndexed { currentIndex, reminder ->
            if (currentIndex == index) {
                reminder.copy(
                    reminderOptionsEnabled = !reminder.reminderOptionsEnabled,
                    minutesError = null
                )
            } else reminder
        }
    }

    fun loadMedicineForEdit(medicine: Medicine) {
        _medicineName.value = medicine.name
        _quantity.value = medicine.quantity
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
                time = "12:00 AM",
                minutes = ""
            )
        )
        _startDate.value = getMalaysiaDate().format(dateFormatter)
        _notes.value = ""
        _presetImageRes.value = null
        _galleryImageUri.value = null

        // Clear field errors
        _medicineNameError.value = null
        _quantityError.value = null
        _dosageAmountError.value = null
        _refillQuantityError.value = null
        _reminderTimeError.value = null
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




}