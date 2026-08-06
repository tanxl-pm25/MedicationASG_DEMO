package com.example.medication_demo.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val CalendarGreen = Color(0xFF159447)
private val CalendarLightGreen = Color(0xFFE8F7ED)
private val CalendarRed = Color(0xFFE53935)
private val CalendarOrange = Color(0xFFF59E0B)
private val CalendarGrey = Color(0xFF6B7280)
private val CalendarLightGrey = Color(0xFFF4F5F6)
private val CalendarDivider = Color(0xFFE5E7EB)

private enum class CalendarMedicineStatus {
    TAKEN,
    MISSING,
    UPCOMING
}

private data class CalendarDayUi(
    val date: LocalDate?,
    val status: CalendarMedicineStatus? = null
)

private data class CalendarMedicineUi(
    val time: String,
    val medicineName: String,
    val dosage: String,
    val status: CalendarMedicineStatus,
    val statusText: String
)

@Composable
fun MedicineCalendarScreen(
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var displayedMonth by remember {
        mutableStateOf(YearMonth.of(2025, 5))
    }

    var selectedDate by remember {
        mutableStateOf(LocalDate.of(2025, 5, 12))
    }

    val medicines = listOf(
        CalendarMedicineUi(
            time = "08:00 AM",
            medicineName = "Vitamin D3",
            dosage = "1 Tablet",
            status = CalendarMedicineStatus.TAKEN,
            statusText = "Taken"
        ),
        CalendarMedicineUi(
            time = "10:00 AM",
            medicineName = "Metformin",
            dosage = "1 Tablet",
            status = CalendarMedicineStatus.TAKEN,
            statusText = "Taken"
        ),
        CalendarMedicineUi(
            time = "08:00 PM",
            medicineName = "Amlodipine",
            dosage = "1 Tablet",
            status = CalendarMedicineStatus.UPCOMING,
            statusText = "Upcoming"
        )
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CalendarTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            MonthSelector(
                displayedMonth = displayedMonth,
                onPreviousMonth = {
                    displayedMonth = displayedMonth.minusMonths(1)

                    selectedDate = displayedMonth.atDay(1)
                },
                onNextMonth = {
                    displayedMonth = displayedMonth.plusMonths(1)

                    selectedDate = displayedMonth.atDay(1)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            WeekdayHeader()

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                displayedMonth = displayedMonth,
                selectedDate = selectedDate,
                onDateSelected = {
                    selectedDate = it
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(color = CalendarDivider)

            Spacer(modifier = Modifier.height(18.dp))

            SelectedDateHeader(
                selectedDate = selectedDate
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProgressCard(
                taken = 2,
                total = 3
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Medication Schedule",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            medicines.forEach { medicine ->
                CalendarMedicineCard(
                    medicine = medicine
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun CalendarTopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 6.dp,
                end = 6.dp,
                top = 28.dp,
                bottom = 8.dp
            )
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Medication Calendar",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = onMoreClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun MonthSelector(
    displayedMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onPreviousMonth
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous month",
                tint = CalendarGreen
            )
        }

        Text(
            text = displayedMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy",Locale.ENGLISH)
            ),
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(
            onClick = onNextMonth
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next month",
                tint = CalendarGreen
            )
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekDays = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        weekDays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = CalendarGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    displayedMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val calendarDays = buildCalendarDays(
        displayedMonth = displayedMonth
    )

    val rowCount = (calendarDays.size + 6) / 7
    val rowHeight = 38.dp
    val rowSpacing = 6.dp

    val gridHeight =
        rowHeight * rowCount +
                rowSpacing * (rowCount - 1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeight),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(calendarDays) { calendarDay ->
            CalendarDayCell(
                calendarDay = calendarDay,
                isSelected = calendarDay.date == selectedDate,
                onClick = {
                    calendarDay.date?.let(onDateSelected)
                }
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    calendarDay: CalendarDayUi,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val date = calendarDay.date

    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(CircleShape)
            .background(
                color = when {
                    isSelected -> CalendarGreen
                    else -> Color.Transparent
                }
            )
            .clickable(
                enabled = date != null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) {
                    Color.White
                } else {
                    Color.Black
                }
            )

            if (!isSelected && calendarDay.status != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                        .size(5.dp)
                        .background(
                            color = when (calendarDay.status) {
                                CalendarMedicineStatus.TAKEN ->
                                    CalendarGreen

                                CalendarMedicineStatus.MISSING ->
                                    CalendarRed

                                CalendarMedicineStatus.UPCOMING ->
                                    CalendarOrange
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun SelectedDateHeader(
    selectedDate: LocalDate
) {
    Column {
        Text(
            text = "Selected Date",
            style = MaterialTheme.typography.bodySmall,
            color = CalendarGrey
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = selectedDate.format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy",Locale.ENGLISH)
            ),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ProgressCard(
    taken: Int,
    total: Int
) {
    val progress = if (total == 0) {
        0f
    } else {
        taken.toFloat() / total.toFloat()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CalendarLightGreen
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Progress",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "$taken / $total Taken",
                    style = MaterialTheme.typography.labelLarge,
                    color = CalendarGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = CalendarGreen,
                trackColor = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% completed",
                style = MaterialTheme.typography.bodySmall,
                color = CalendarGrey
            )
        }
    }
}

@Composable
private fun CalendarMedicineCard(
    medicine: CalendarMedicineUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = CalendarLightGrey
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = medicine.time,
                modifier = Modifier.weight(0.9f),
                style = MaterialTheme.typography.bodyMedium
            )

            when (medicine.status) {

                CalendarMedicineStatus.TAKEN -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Taken",
                        tint = CalendarGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }

                CalendarMedicineStatus.MISSING -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Missing",
                        tint = CalendarRed,
                        modifier = Modifier.size(26.dp)
                    )
                }

                CalendarMedicineStatus.UPCOMING -> {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Upcoming",
                        tint = CalendarOrange,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1.6f)
            ) {
                Text(
                    text = medicine.medicineName,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = medicine.dosage,
                    style = MaterialTheme.typography.bodySmall,
                    color = CalendarGrey
                )
            }

            Text(
                text = medicine.statusText,
                style = MaterialTheme.typography.labelMedium,
                color = when (medicine.status) {
                    CalendarMedicineStatus.TAKEN ->
                        CalendarGreen

                    CalendarMedicineStatus.MISSING ->
                        CalendarRed

                    CalendarMedicineStatus.UPCOMING ->
                        CalendarOrange
                }
            )
        }
    }
}

private fun buildCalendarDays(
    displayedMonth: YearMonth
): List<CalendarDayUi> {
    val firstDay = displayedMonth.atDay(1)

    val leadingEmptyDays = when (firstDay.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }

    val days = mutableListOf<CalendarDayUi>()

    repeat(leadingEmptyDays) {
        days.add(
            CalendarDayUi(date = null)
        )
    }

    for (day in 1..displayedMonth.lengthOfMonth()) {
        val date = displayedMonth.atDay(day)

        val status = when (day) {
            5, 6, 7, 8, 9, 10, 11, 12 ->
                CalendarMedicineStatus.TAKEN

            14, 21 ->
                CalendarMedicineStatus.MISSING

            13, 15, 16 ->
                CalendarMedicineStatus.UPCOMING

            else -> null
        }

        days.add(
            CalendarDayUi(
                date = date,
                status = status
            )
        )
    }

    while (days.size % 7 != 0) {
        days.add(
            CalendarDayUi(date = null)
        )
    }

    return days
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MedicineCalendarScreenPreview() {
    Medication_DemoTheme {
        MedicineCalendarScreen()
    }
}
