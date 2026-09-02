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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medication_demo.ui.AppTopBar
import kotlinx.coroutines.delay
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
    onViewCalendarClick: () -> Unit = {},
    medicineVm: MedicineViewModel = viewModel(),
    medicineListVm: MedicineListViewModel = viewModel()
) {
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val archivedMedicines by medicineVm.archivedMedicines.collectAsStateWithLifecycle()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val today = getMalaysiaDate()
    val refreshTime by produceState(
        initialValue = getMalaysiaTime()
    ) {
        while (true) {
            value = getMalaysiaTime()
            delay(1000)
        }
    }
    val formattedToday =
        today.format(
            DateTimeFormatter.ofPattern(
                "EEE, dd MMM",
                Locale.ENGLISH
            )
        )
    val scheduleItems =
        remember(
            medicines,
            archivedMedicines,
            takenRecords,
            rescheduledDoses,
            today,
            refreshTime
        ) {
            medicineListVm.getEffectiveDosesForDate(
                medicines = medicines,
                archivedMedicines = archivedMedicines,
                date = today,
                takenRecords = takenRecords,
                rescheduledDoses = rescheduledDoses,
                medicineVm = medicineVm
            )
        }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ScheduleBackground,
        topBar = {
            AppTopBar(
                title = "Medicine Schedule",
                onBackClick = onBackClick,
                showMoreMenu = true,
                onHelpClick = {
                    showHelpDialog = true
                }
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

            if (scheduleItems.isEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "No medication scheduled",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ScheduleGrey
                )
            } else {
                scheduleItems.forEachIndexed { index, item ->
                    MedicineScheduleRow(item = item)
                    if (index != scheduleItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = ScheduleDivider
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = {
                showHelpDialog = false
            },
            title = { Text("Schedule Help")
            },

            text = {
                Text("You can view your daily schedule at here, ")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showHelpDialog = false
                    }
                ) {
                    Text("Got it")
                }
            }
        )
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
            modifier = Modifier.width(72.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        // STATUS ICON
        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            StatusIcon(status = item.status)
        }

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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.status.displayText,
                style = MaterialTheme.typography.bodyMedium,
                color = when (item.status) {
                    DoseStatus.TAKEN -> ScheduleGreen
                    DoseStatus.IN_PROGRESS -> ScheduleGreen
                    DoseStatus.MISSING -> ScheduleRed
                    DoseStatus.UPCOMING -> ScheduleOrange
                }
            )
            if (
                item.status == DoseStatus.TAKEN && item.takenTime != null
            ) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.takenTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = ScheduleGreen
                )
            }
        }
    }
}

@Composable
private fun StatusIcon(
    status: DoseStatus
) {
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
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            DoseStatus.IN_PROGRESS -> {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "In Progress",
                    tint = ScheduleGreen,
                    modifier = Modifier.size(35.dp)
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
                    tint = ScheduleOrange,
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