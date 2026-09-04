package com.example.medication_demo.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Medication
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.viewmodel.AppBottomNavigationBar
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedButton
import com.example.medication_demo.model.DoseStatus
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.medication_demo.components.MainScreenActionIcon
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.medication_demo.components.MedicationTimePickerDialog
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime

private val HomeGreen = Color(0xFF159447)
private fun getCurrentDate(): String {
    val formatter =
        DateTimeFormatter.ofPattern(
            "EEEE, d MMMM",
            Locale.ENGLISH
        )
    return getMalaysiaDate().format(formatter)
}

@Composable
fun HomeScreen(
    username: String,
    date: String = getCurrentDate(),
    nextMedicineName: String? = null,
    nextMedicineDose: String ?= null,
    nextMedicineTime: String? = null,
    medicinesTaken: String? = null,
    medicinesTotal: Int = 0,
    upcomingAppointments: String? = null,
    waterGlasses: String? = null,
    waterGoal: Int = 0,
    monthlyStatText: String? = null,
    hasUnreadNotofication: Boolean = false,
    onMarkAsTakenClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBottomNavSelected: (Int) -> Unit = {},
    onMedicinesClick: () -> Unit = {},
    onAppointmentClick: () -> Unit ={},
    onWaterIntakeClick: () -> Unit = {},
    onMonthlyStatisticsClick: () -> Unit = {},
    nextMedicineStatus: DoseStatus? = null,
    onRescheduleConfirm: (String) -> Unit = {}
) {
    var showRescheduleTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern(
            "hh:mm a",
            Locale.ENGLISH
        )
    }
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            AppBottomNavigationBar(
                selectedIndex = 0,
                onSelected = onBottomNavSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append("Good Morning,\n")
                            withStyle(
                                style = SpanStyle(
                                    color = HomeGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(username)
                            }
                            append(" \uD83D\uDC4B")
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MainScreenActionIcon(
                    icon = Icons.Filled.NotificationsNone,
                    contentDescription = "Notifications",
                    onClick = onNotificationClick,
                    showBadge = hasUnreadNotofication
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Medicine
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HomeGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Next Medicine",
                        fontSize = 15.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = nextMedicineName ?: "--",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nextMedicineDose ?: "--",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(
                            modifier = Modifier.padding(end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = nextMedicineTime ?: "--",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (nextMedicineStatus) {
                        DoseStatus.MISSING -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onMarkAsTakenClick,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 10.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = HomeGreen
                                    )
                                ) {
                                    Text(
                                        text = "Mark as Taken",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        showRescheduleTimePicker = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 8.dp,
                                        vertical = 10.dp
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = Color.White
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = HomeGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "Reschedule",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                        DoseStatus.IN_PROGRESS,
                        DoseStatus.UPCOMING -> {
                            OutlinedButton(
                                onClick = onMarkAsTakenClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = HomeGreen
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = HomeGreen
                                )
                            ) {
                                Text(
                                    text = "Mark as Taken",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Medication,
                    title = "Medicines",
                    valueBold = medicinesTaken,
                    valueRest = " / $medicinesTotal Taken",
                    fontSize = 13.sp,
                    onClick = onMedicinesClick
                )
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.CalendarMonth,
                    title = "Appointments",
                    valueBold = upcomingAppointments,
                    fontSize = 13.sp,
                    valueRest = " Upcoming",
                    onClick = onAppointmentClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.WaterDrop,
                    title = "Water Intake",
                    valueBold =
                        if (waterGoal > 0) {
                            waterGlasses
                        }else{
                            ""
                        },
                    valueRest =
                        if (waterGoal > 0) {
                            " / $waterGoal Glasses"
                        } else {
                            " Set your daily goal"
                        },
                    fontSize = 13.sp,
                    onClick = onWaterIntakeClick
                )
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.BarChart,
                    title = "Monthly Statistics",
                    valueBold = monthlyStatText ?: "",

                    valueRest =
                        if (monthlyStatText != null) {
                            "%"
                        } else {
                            "No record found"
                        },
                    fontSize = 8.sp,
                    onClick = onMonthlyStatisticsClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Encouragement banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = HomeGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Keep it up! You're doing great!",
                    color = HomeGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    if (showRescheduleTimePicker) {
        MedicationTimePickerDialog(
            initialTime = getMalaysiaTime(),
            onDismiss = {
                showRescheduleTimePicker = false
            },
            validateTime = { selectedTime ->
                val now = getMalaysiaTime()
                if (!selectedTime.isAfter(now)) {
                    "Please select a future time."
                } else {
                    null
                }
            },
            onConfirm = { selectedTime ->
                showRescheduleTimePicker = false
                onRescheduleConfirm(
                    selectedTime.format(timeFormatter)
                )
            }
        )
    }
}
@Composable
private fun OverviewCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    valueBold: String?,
    valueRest: String = "",
    footer: String? = null,
    fontSize: TextUnit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(22.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View details",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (valueBold == null){
                Text(
                    text = "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            } else{
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        ){
                            append(valueBold)
                        }
                        append(valueRest)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (footer != null){
            Text(
                text = footer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    Medication_DemoTheme {
        HomeScreen(username = "")
    }
}