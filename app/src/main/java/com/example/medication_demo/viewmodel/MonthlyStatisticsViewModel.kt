package com.example.medication_demo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medication_demo.model.ArchivedMedicine
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.model.MedicationTakenRecord
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.model.MonthlyStatisticsUiState
import com.example.medication_demo.model.RescheduledDose
import com.example.medication_demo.utils.getMalaysiaDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

class MonthlyStatisticsViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            MonthlyStatisticsUiState()
        )

    val uiState: StateFlow<MonthlyStatisticsUiState> =
        _uiState.asStateFlow()

    private var medicines =
        emptyList<Medicine>()

    private var archivedMedicines =
        emptyList<ArchivedMedicine>()

    private var takenRecords =
        emptyList<MedicationTakenRecord>()

    private var rescheduledDoses =
        emptyList<RescheduledDose>()

    private var medicineListVm:
            MedicineListViewModel? = null

    private var medicineVm:
            MedicineViewModel? = null

    fun updateRecords(
        medicines: List<Medicine>,
        archivedMedicines: List<ArchivedMedicine>,
        takenRecords: List<MedicationTakenRecord>,
        rescheduledDoses: List<RescheduledDose>,
        medicineListVm: MedicineListViewModel,
        medicineVm: MedicineViewModel
    ) {
        this.medicines = medicines
        this.archivedMedicines = archivedMedicines
        this.takenRecords = takenRecords
        this.rescheduledDoses = rescheduledDoses
        this.medicineListVm = medicineListVm
        this.medicineVm = medicineVm

        updateStatistics()
    }

    fun previousMonth() {
        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    _uiState.value.selectedMonth
                        .minusMonths(1)
            )

        updateStatistics()
    }

    fun nextMonth() {
        val currentMonth =
            YearMonth.from(
                getMalaysiaDate()
            )

        if (
            !_uiState.value.selectedMonth
                .isBefore(currentMonth)
        ) {
            return
        }

        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    _uiState.value.selectedMonth
                        .plusMonths(1)
            )

        updateStatistics()
    }

    fun selectMonth(
        year: Int,
        month: Int
    ) {
        _uiState.value =
            _uiState.value.copy(
                selectedMonth =
                    YearMonth.of(
                        year,
                        month
                    )
            )

        updateStatistics()
    }

    private fun updateStatistics() {

        val listVm =
            medicineListVm
                ?: return

        val medVm =
            medicineVm
                ?: return

        val selectedMonth =
            _uiState.value.selectedMonth

        val today =
            getMalaysiaDate()

        val startDate =
            selectedMonth.atDay(1)

        val monthEnd =
            selectedMonth.atEndOfMonth()

        val endDate =
            if (
                selectedMonth ==
                YearMonth.from(today)
            ) {
                today
            } else {
                monthEnd
            }

        val allDoses =
            generateSequence(startDate) {
                it.plusDays(1)
            }
                .takeWhile {
                    !it.isAfter(endDate)
                }
                .flatMap { date ->

                    listVm
                        .getEffectiveDosesForDate(
                            medicines = medicines,
                            archivedMedicines =
                                archivedMedicines,
                            date = date,
                            takenRecords =
                                takenRecords,
                            rescheduledDoses =
                                rescheduledDoses,
                            medicineVm = medVm
                        )
                        .asSequence()
                        .map { dose ->
                            date to dose
                        }
                }
                .toList()

        val takenCount =
            allDoses.count { (_, dose) ->
                dose.status ==
                        DoseStatus.TAKEN
            }

        val missedCount =
            allDoses.count { (_, dose) ->
                dose.status ==
                        DoseStatus.MISSING
            }

        val totalCount =
            takenCount +
                    missedCount

        val percentage =
            if (totalCount == 0) {
                0
            } else {
                (
                        takenCount.toFloat() /
                                totalCount.toFloat() *
                                100
                        ).roundToInt()
            }

        val message =
            when {
                totalCount == 0 ->
                    "No medication data yet."

                percentage >= 90 ->
                    "Excellent progress!"

                percentage >= 75 ->
                    "Well done! Keep it up."

                percentage >= 50 ->
                    "You're improving!"

                else ->
                    "Let's get back on track."
            }

        val daysPerBar =
            (
                    selectedMonth
                        .lengthOfMonth() + 7
                    ) / 8

        val chartRanges =
            List(8) { index ->

                val startDay =
                    index *
                            daysPerBar + 1

                val endDay =
                    minOf(
                        startDay +
                                daysPerBar - 1,
                        selectedMonth
                            .lengthOfMonth()
                    )

                startDay to endDay
            }

        val chartValues =
            chartRanges.map { range ->

                val dosesForRange =
                    allDoses.filter { (date, _) ->
                        date.dayOfMonth in
                                range.first..range.second
                    }

                val takenForRange =
                    dosesForRange.count { (_, dose) ->
                        dose.status ==
                                DoseStatus.TAKEN
                    }

                val missedForRange =
                    dosesForRange.count { (_, dose) ->
                        dose.status ==
                                DoseStatus.MISSING
                    }

                val totalForRange =
                    takenForRange +
                            missedForRange

                if (totalForRange == 0) {
                    0
                } else {
                    (
                            takenForRange.toFloat() /
                                    totalForRange.toFloat() *
                                    100
                            ).roundToInt()
                }
            }

        _uiState.value =
            _uiState.value.copy(
                adherencePercentage =
                    percentage,
                adherenceMessage =
                    message,
                takenDoses =
                    takenCount,
                missedDoses =
                    missedCount,
                totalDoses =
                    totalCount,
                chartValues =
                    chartValues,
                chartLabels =
                    chartRanges.map {
                        it.first.toString()
                    }
            )
    }
}