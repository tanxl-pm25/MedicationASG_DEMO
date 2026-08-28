package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class MedicineStatus {
    ACTIVE,
    UPCOMING,
    COMPLETED
}
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

    private fun calculateCustomEndDate(
        medicine: Medicine,
        startDate: LocalDate,
        totalDoses: Long
    ): LocalDate? {
        val parts =
            medicine.frequency
                .lowercase()
                .split(" ")
        if (
            parts.size < 3 ||
            parts[0] != "every"
        ) {
            return null
        }
        val amount = parts[1].toLongOrNull() ?: return null
        if (amount <= 0) {
            return null
        }
        val unit = parts[2]
        return when (unit) {
            "hour", "hours" -> {
                val totalHours = (totalDoses - 1) * amount
                startDate.plusDays(
                    totalHours / 24
                )
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

        val today = LocalDate.now()

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


}

