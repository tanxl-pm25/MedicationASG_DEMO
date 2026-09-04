package com.example.medication_demo.appointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.R
import com.example.medication_demo.model.AppointmentStatus
import com.example.medication_demo.reminder.AppointmentReminderScheduler
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.viewmodel.AppointmentDetailsViewModel
import com.example.medication_demo.viewmodel.AppointmentDetailsViewModelFactory

@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    onRescheduleClick: () -> Unit = {},
    viewModel: AppointmentDetailsViewModel = viewModel(
        factory = AppointmentDetailsViewModelFactory(
            appointmentId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(48.dp)
                    .clickable {
                        onBackClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Appointment Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id = doctorAvatarFor(uiState.doctor)
                    ),
                    contentDescription = "Doctor photo",
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = uiState.doctor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiState.appointmentName,
                        fontSize = 14.sp,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val reminderText =
            when (uiState.reminderMinutesBefore) {
                0 -> "At appointment time"
                5 -> "5 minutes before"
                15 -> "15 minutes before"
                30 -> "30 minutes before"
                60 -> "1 hour before"
                1440 -> "1 day before"
                null -> "No reminder set"
                else ->
                    "${uiState.reminderMinutesBefore} minutes before"
            }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 10.dp
                )
            ) {
                DetailRow(
                    icon = Icons.Default.CalendarToday,
                    title = "Date",
                    value = uiState.date
                )

                DetailDivider()

                DetailRow(
                    icon = Icons.Default.Schedule,
                    title = "Time",
                    value = uiState.time
                )

                DetailDivider()

                DetailRow(
                    icon = Icons.Default.NotificationsNone,
                    title = "Reminder",
                    value = reminderText
                )

                DetailDivider()

                DetailRow(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    value = uiState.location
                )

                if (uiState.purpose.isNotBlank()) {
                    DetailDivider()

                    DetailRow(
                        icon = Icons.Default.MedicalInformation,
                        title = "Purpose",
                        value = uiState.purpose
                    )
                }

                if (uiState.notes.isNotBlank()) {
                    DetailDivider()

                    DetailRow(
                        icon = Icons.Default.Notes,
                        title = "Notes",
                        value = uiState.notes
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface,
                    contentColor =
                        MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.55f
                        )
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Edit",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface,
                    contentColor =
                        MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        MaterialTheme.colorScheme.error.copy(
                            alpha = 0.55f
                        )
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Delete",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (
            uiState.status == AppointmentStatus.MISSED ||
            uiState.status == AppointmentStatus.CANCELLED
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onRescheduleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary,
                    contentColor =
                        MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Reschedule Appointment",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor =
                MaterialTheme.colorScheme.onSurface,
            textContentColor =
                MaterialTheme.colorScheme.onSurfaceVariant,
            title = {
                Text(
                    text = "Delete Appointment"
                )
            },
            text = {
                Text(
                    text =
                        "Are you sure you want to delete " +
                                "${uiState.appointmentName} with " +
                                "${uiState.doctor}?\n\n" +
                                "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        AppointmentReminderScheduler.cancel(
                            context = context,
                            appointmentId = appointmentId
                        )

                        viewModel.deleteAppointment()

                        showDeleteConfirmation = false
                        onDeleteSuccess()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

private fun doctorAvatarFor(
    doctorName: String
): Int {
    return if (
        (doctorName.hashCode() and 1) == 0
    ) {
        R.drawable.doctor_male
    } else {
        R.drawable.doctor_female
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.width(22.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 42.dp),
        color = MaterialTheme.colorScheme.outline,
        thickness = 1.dp
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AppointmentDetailsScreenPreview() {
    Medication_DemoTheme {
        AppointmentDetailsScreen(
            appointmentId = 1
        )
    }
}