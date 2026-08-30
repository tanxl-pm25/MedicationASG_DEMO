package com.example.medication_demo.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.model.MedicineDoseUi
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.viewmodel.MedicineViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medication_demo.utils.getMalaysiaTime

private val ScheduleGreen = Color(0xFF159447)
private val ScheduleRed = Color(0xFFE53935)
private val ScheduleOrange = Color(0xFFF59E0B)
private val ScheduleGrey = Color(0xFF6B7280)
private val ScheduleDivider = Color(0xFFE5E7EB)
private val ScheduleBackground = Color.White

@Composable
fun MedicineScheduleScreen(
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onViewCalendarClick: () -> Unit = {},
    medicineVm: MedicineViewModel = viewModel(),
    medicineListVm: MedicineListViewModel = viewModel()
) {
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val today = getMalaysiaDate()
    val formattedToday =
        today.format(
            DateTimeFormatter.ofPattern(
                "EEE, dd MMM",
                Locale.ENGLISH
            )
        )
    val scheduleItems =
        medicines
            .filter { medicine ->
                medicineListVm.isMedicineActiveOnDate(
                    medicine = medicine,
                    date = today
                )
            }
            .flatMap { medicine ->
                medicineListVm.createMedicineDoseUiList(
                    medicine = medicine,
                    date = today,
                    takenRecords = takenRecords,
                    rescheduledDoses = rescheduledDoses
                )
            }
    Scaffold(
        containerColor = ScheduleBackground,
        topBar = {
            MedicineScheduleTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        start = 18.dp,
                        end = 18.dp,
                        top = 14.dp,
                        bottom = 32.dp
                    )
            ) {
                OutlinedButton(
                    onClick = onViewCalendarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = ScheduleGreen
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ScheduleGreen
                    )
                ) {
                    Text(
                        text = "View Calendar",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = formattedToday,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "TIME",
                    modifier = Modifier.weight(0.9f),
                    style = MaterialTheme.typography.labelMedium,
                    color = ScheduleGrey
                )

                Spacer(modifier = Modifier.width(42.dp))

                Text(
                    text = "MEDICINE",
                    modifier = Modifier.weight(1.5f),
                    style = MaterialTheme.typography.labelMedium,
                    color = ScheduleGrey
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "STATUS",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = ScheduleGrey
                )
            }

            HorizontalDivider(
                color = ScheduleDivider
            )

            Spacer(modifier = Modifier.height(16.dp))

            scheduleItems.forEachIndexed { index, item ->
                MedicineScheduleRow(item = item)

                if (index != scheduleItems.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = ScheduleDivider
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MedicineScheduleTopBar(
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
            ),
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
            text = "Medicine Schedule",
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
private fun MedicineScheduleRow(
    item: MedicineDoseUi
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = item.time,
            modifier = Modifier.weight(0.9f),
            style = MaterialTheme.typography.bodyMedium
        )

        StatusIcon(
            status = item.status
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1.5f)
        ) {
            Text(
                text = item.medicineName,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = item.dosage,
                style = MaterialTheme.typography.bodySmall,
                color = ScheduleGrey
            )

            if (item.extraText != null) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = item.extraText,
                    style = MaterialTheme.typography.bodySmall,
                    color = ScheduleRed
                )
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = item.status.displayText,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = when (item.status) {
                DoseStatus.TAKEN -> ScheduleGreen
                DoseStatus.MISSING -> ScheduleRed
                DoseStatus.UPCOMING -> ScheduleOrange
            }
        )
    }
}

@Composable
private fun StatusIcon(
    status: DoseStatus
) {
    val backgroundColor = when (status) {
        DoseStatus.TAKEN -> ScheduleGreen
        DoseStatus.MISSING -> ScheduleRed
        DoseStatus.UPCOMING -> Color.Transparent
    }

    val borderColor = when (status) {
        DoseStatus.TAKEN -> ScheduleGreen
        DoseStatus.MISSING -> ScheduleRed
        DoseStatus.UPCOMING -> ScheduleOrange
    }

    val iconColor = when (status) {
        DoseStatus.TAKEN -> Color.White
        DoseStatus.MISSING -> Color.White
        DoseStatus.UPCOMING -> ScheduleOrange
    }

    Box(
        modifier = Modifier
            .then(
                if (status == DoseStatus.TAKEN) {
                    Modifier.size(30.dp)
                        .background(
                            color = ScheduleGreen,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = ScheduleGreen,
                            shape = CircleShape
                        )
                } else {
                    Modifier.size(35.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            DoseStatus.TAKEN -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Taken",
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            DoseStatus.MISSING -> {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Missing",
                    tint = ScheduleRed,
                    modifier = Modifier.size(35.dp)
                )
            }

            DoseStatus.UPCOMING -> {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Upcoming",
                    tint = iconColor,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MedicineScheduleScreenPreview() {
    Medication_DemoTheme {
        MedicineScheduleScreen()
    }
}