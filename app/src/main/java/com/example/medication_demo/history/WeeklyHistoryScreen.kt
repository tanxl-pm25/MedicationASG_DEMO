package com.example.medication_demo.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.ui.text.style.TextOverflow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medication_demo.components.MainScreenTopBar
import com.example.medication_demo.medication.MedicineImage
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.viewmodel.AppBottomNavigationBar
import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.viewmodel.MedicineViewModel
import com.example.medication_demo.viewmodel.WeeklyHistoryViewModel
import com.example.medication_demo.model.MedicineHistoryUi
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.model.HistoryMedicineSource
import com.example.medication_demo.utils.isDoseBeforeMedicineDeletion
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.medication_demo.components.InfoGuideDialog
import com.example.medication_demo.R
import com.example.medication_demo.components.MainScreenActionIcon


private val HistoryGreen = Color(0xFF159447)
private val HistoryRed = Color(0xFFE53935)
private val HistoryGrey = Color(0xFF6B7280)
private val HistoryCardBackground = Color(0xFFF8F8F8)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyHistoryScreen(
    onMoreClick: () -> Unit = {},
    onBottomNavSelected: (Int) -> Unit = {},
    onMedicineClick: (
        medicineId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ) -> Unit,
    historyVm: WeeklyHistoryViewModel,
    medicineVm: MedicineViewModel = viewModel(),
    medicineListVm: MedicineListViewModel = viewModel()
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val selectedStartDate by historyVm.selectedStartDate.collectAsStateWithLifecycle()
    val selectedEndDate by historyVm.selectedEndDate.collectAsStateWithLifecycle()
    val showDateRangePicker by historyVm.showDateRangePicker.collectAsStateWithLifecycle()
    val dateRangeError by historyVm.dateRangeError.collectAsStateWithLifecycle()
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val archivedMedicines by medicineVm.archivedMedicines.collectAsStateWithLifecycle()
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern(
            "dd MMM yyyy",
            Locale.ENGLISH
        )
    }
    val historySources =
        medicines.map { medicine ->
            HistoryMedicineSource(
                medicine = medicine
            )
        } + archivedMedicines.map { archived ->
            HistoryMedicineSource(
                medicine = archived.medicine,
                deletedDate = archived.deletedDate,
                deletedTime = archived.deletedTime
            )
        }
    val historyMedicines =
        historySources.mapNotNull { source ->
            val medicine = source.medicine
            val deletedDate = source.deletedDate
            val deletedTime = source.deletedTime

            val dosageText = medicineListVm.getDosageText(medicine)
            val dates =
                generateSequence(selectedStartDate) {
                    it.plusDays(1)
                }
                    .takeWhile {
                        !it.isAfter(selectedEndDate)
                    }
                    .filter { date ->
                        // Cannot show future history
                        !date.isAfter(getMalaysiaDate()) &&
                                // Archived medicine should stop generating history after deletion
                                (deletedDate == null || !date.isAfter(deletedDate))
                    }
                    .toList()

            val dosesInRange =
                dates.flatMap { date ->
                    val historicalMedicine =
                        medicineVm.getMedicineForHistoricalDate(medicine = medicine, date = date)
                    val doses =
                        medicineListVm
                            .getMedicineDosesForDate(
                                medicine = historicalMedicine,
                                date = date,
                                takenRecords = takenRecords,
                                rescheduledDoses = rescheduledDoses
                            )
                            .filter { dose ->
                                val isBeforeDeletion =
                                    isDoseBeforeMedicineDeletion(
                                        medicineId = medicine.id,
                                        date = date,
                                        doseTime = dose.time,
                                        archivedMedicines = archivedMedicines
                                    )
                                val isHistoryStatus =
                                    dose.status == DoseStatus.TAKEN || dose.status == DoseStatus.MISSING
                                isBeforeDeletion && isHistoryStatus
                            }

                    doses
                }

            val takenCount =
                dosesInRange.count {
                    it.status == DoseStatus.TAKEN
                }
            val missingCount =
                dosesInRange.count {
                    it.status == DoseStatus.MISSING
                }
            if (
                takenCount == 0 &&
                missingCount == 0
            ) {
                null
            } else {
                MedicineHistoryUi(
                    medicineId = medicine.id,
                    name = medicine.name,
                    dosage = dosageText,
                    time =
                        when {
                            medicine.frequency.equals(
                                "As needed",
                                ignoreCase = true
                            ) -> {
                                val takenTimes =
                                    dosesInRange
                                        .filter {
                                            it.status == DoseStatus.TAKEN
                                        }
                                        .map {
                                            it.takenTime ?: it.time
                                        }
                                when {
                                    takenTimes.isEmpty() -> "-"
                                    takenTimes.size == 1 -> takenTimes.first()
                                    else -> takenTimes.joinToString(", ")
                                }
                            }
                            medicineListVm.isHourlyFrequency(medicine) -> {
                                "From ${
                                    medicine.reminderTimes
                                        .firstOrNull()
                                        ?.time
                                        ?: "-"
                                }"
                            }
                            else -> {
                                medicine.reminderTimes
                                    .joinToString(", ") {
                                        it.time
                                    }
                            }
                        },
                    frequency = medicine.frequency,
                    takenCount = takenCount,
                    missingCount = missingCount,
                    presetImageRes = medicine.presetImageRes,
                    galleryImageUri = medicine.galleryImageUri
                )
            }
        }
    val totalTaken =
        historyMedicines.sumOf {
            it.takenCount
        }

    val totalMissing =
        historyMedicines.sumOf {
            it.missingCount
        }

    val totalDoses =
        totalTaken + totalMissing

    val overallAdherence =
        if (totalDoses > 0) {
            totalTaken * 100 / totalDoses
        } else {
            0
        }
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            AppBottomNavigationBar(
                selectedIndex = 2,
                onSelected = onBottomNavSelected
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            MainScreenTopBar(
                title = "Weekly History",
                titleStartPadding = 10.dp,
                rightContent = {
                    Box {
                        MainScreenActionIcon(
                            icon = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            onClick = {
                                showMoreMenu = true
                            }
                        )

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = {
                                showMoreMenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Help")
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showHelpDialog = true
                                }
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            WeeklyDateRangeSelector(
                startDate = selectedStartDate,
                endDate = selectedEndDate,
                formatter = dateFormatter,
                onClick = historyVm::openDateRangePicker
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (historyMedicines.isNotEmpty()) {
                WeeklySummaryCard(
                    takenCount = totalTaken,
                    missingCount = totalMissing,
                    adherence = overallAdherence
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (historyMedicines.isEmpty()) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "No history record found",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HistoryGrey
                )
            } else {
                historyMedicines.forEach { medicine ->
                    HistoryMedicineCard(
                        medicine = medicine,
                        onClick = {
                            onMedicineClick(
                                medicine.medicineId,
                                selectedStartDate,
                                selectedEndDate
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    if (showHelpDialog) {

        InfoGuideDialog(
            title = "Weekly History Help",
            imageRes = R.drawable.weeklyhistory,
            description =
                buildAnnotatedString {
                    append("Select a ")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF009688),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("\"7 - day date range\"")
                    }
                    append(" to review your")
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF009688),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("medication history")
                    }
                    append("You can check taken and missed doses and tap a medicine to view more details.")
                },
            onDismiss = {
                showHelpDialog = false
            }
        )
    }

    if (showDateRangePicker) {
        WeeklyDateRangeDialog(
            initialStartDate = selectedStartDate,
            initialEndDate = selectedEndDate,
            errorMessage = dateRangeError,
            onDismiss = historyVm::closeDateRangePicker,
            onConfirm = historyVm::confirmDateRange
        )
    }
}

@Composable
private fun WeeklyDateRangeSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    formatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${startDate.format(formatter)} - " +
                    endDate.format(formatter),
            style = MaterialTheme.typography.bodySmall,
            color = HistoryGrey,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(7.dp))

        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Select date range",
            tint = HistoryGrey,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(2.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Open calendar",
            tint = HistoryGrey,
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeeklyDateRangeDialog(
    initialStartDate: LocalDate,
    initialEndDate: LocalDate,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis =
            initialStartDate.toUtcMillis(),

        initialSelectedEndDateMillis =
            initialEndDate.toUtcMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis =
                        dateRangePickerState
                            .selectedStartDateMillis

                    val endMillis =
                        dateRangePickerState
                            .selectedEndDateMillis

                    if (
                        startMillis != null &&
                        endMillis != null
                    ) {
                        onConfirm(
                            startMillis.toLocalDateUtc(),
                            endMillis.toLocalDateUtc()
                        )
                    }
                },
                enabled =
                    dateRangePickerState
                        .selectedStartDateMillis != null &&
                            dateRangePickerState
                                .selectedEndDateMillis != null
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = HistoryGreen
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = HistoryGrey
                )
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select 7-Day Range",
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 18.dp
                    ),
                    style =
                        MaterialTheme.typography.titleMedium
                )
            },
            headline = {
                val startDate =
                    dateRangePickerState
                        .selectedStartDateMillis
                        ?.toLocalDateUtc()

                val endDate =
                    dateRangePickerState
                        .selectedEndDateMillis
                        ?.toLocalDateUtc()

                Column(
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = when {
                            startDate != null && endDate != null -> {
                                "${
                                    startDate.format(
                                        DateTimeFormatter.ofPattern(
                                            "dd MMM",
                                            Locale.ENGLISH
                                        )
                                    )
                                } - ${
                                    endDate.format(
                                        DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy",
                                            Locale.ENGLISH
                                        )
                                    )
                                }"
                            }

                            startDate != null -> {
                                "${
                                    startDate.format(
                                        DateTimeFormatter.ofPattern(
                                            "dd MMM yyyy",
                                            Locale.ENGLISH
                                        )
                                    )
                                } - Select end date"
                            }

                            else -> {
                                "Select start and end date"
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = HistoryRed
                        )
                    }
                }
            },
            showModeToggle = false,
            modifier = Modifier.height(500.dp)
        )
    }
}

@Composable
private fun HistoryMedicineCard(
    medicine: MedicineHistoryUi,
    onClick: () -> Unit
) {
    val total =
        medicine.takenCount +
                medicine.missingCount

    val adherence =
        if (total > 0) {
            medicine.takenCount * 100 / total
        } else {
            0
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = HistoryCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // Medicine name + arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medicine.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View history details",
                    tint = HistoryGrey,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = medicine.dosage,
                style = MaterialTheme.typography.bodySmall,
                color = HistoryGrey
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "${medicine.time} • ${medicine.frequency}",
                style = MaterialTheme.typography.bodySmall,
                color = HistoryGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Taken / Missed badges
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                HistoryStatusBadge(
                    text =
                        "${medicine.takenCount} Taken",
                    backgroundColor =
                        HistoryGreen.copy(
                            alpha = 0.12f
                        ),
                    textColor = HistoryGreen
                )

                HistoryStatusBadge(
                    text =
                        "${medicine.missingCount} Missed",
                    backgroundColor =
                        HistoryRed.copy(
                            alpha = 0.10f
                        ),
                    textColor = HistoryRed
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // Adherence
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "Adherence",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color = HistoryGrey
                )

                Text(
                    text = "$adherence%",
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = HistoryGreen
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            LinearProgressIndicator(
                progress = {
                    adherence / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
private fun HistoryStatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape =
                    RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = text,
            style =
                MaterialTheme
                    .typography
                    .labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WeeklySummaryCard(
    takenCount: Int,
    missingCount: Int,
    adherence: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                SummaryStatBox(
                    modifier = Modifier.weight(1f),
                    value = takenCount.toString(),
                    label = "Taken",
                    valueColor = HistoryGreen
                )

                SummaryStatBox(
                    modifier = Modifier.weight(1f),
                    value = missingCount.toString(),
                    label = "Missed",
                    valueColor = HistoryRed
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Adherence",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = HistoryGrey
                )

                Text(
                    text = "$adherence%",
                    style =
                        MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = HistoryGreen
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            LinearProgressIndicator(
                progress = {
                    adherence / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
private fun SummaryStatBox(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    valueColor: Color
) {
    Column(
        modifier = modifier
            .background(
                color = valueColor.copy(
                    alpha = 0.08f
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = HistoryGrey
        )
    }
}
private fun LocalDate.toUtcMillis(): Long {
    return atStartOfDay(
        ZoneOffset.UTC
    ).toInstant().toEpochMilli()
}

private fun Long.toLocalDateUtc(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

/*@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun WeeklyHistoryScreenPreview() {
    Medication_DemoTheme {
        WeeklyHistoryScreen()
    }
}*/
