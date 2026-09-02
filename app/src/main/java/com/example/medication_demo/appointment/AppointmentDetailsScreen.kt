package com.example.medication_demo.appointment

import androidx.compose.material3.AlertDialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.medication_demo.R
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.AppointmentDetailsViewModel
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.MedicalInformation
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.model.AppointmentStatus
import com.example.medication_demo.viewmodel.AppointmentDetailsViewModelFactory

private val AppGreen = Color(0xFF16843A)
private val LightGreen = Color(0xFFE6F4EA)
private val TextGrey = Color(0xFF757575)
private val DarkText = Color(0xFF17233C)
private val SoftGrey = Color(0xFFF5F5F5)
private val Red = Color(0xFFFF3B30)

@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    onRescheduleClick: () -> Unit = {},
    viewModel: AppointmentDetailsViewModel = viewModel(
        factory = AppointmentDetailsViewModelFactory(appointmentId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirmation by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {

        // TOP BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            contentAlignment = Alignment.Center
        ) {

            // Back Arrow
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
                    tint = DarkText,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Appointment Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            // More
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = DarkText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Doctor Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
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

                //  doctor image placeholder
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
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiState.appointmentName,
                        fontSize = 14.sp,
                        color = TextGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // APPOINTMENT INFORMATION
        val reminderText = when (uiState.reminderMinutesBefore) {
            0 -> "At appointment time"
            5 -> "5 minutes before"
            15 -> "15 minutes before"
            30 -> "30 minutes before"
            60 -> "1 hour before"
            1440 -> "1 day before"
            null -> "No reminder set"
            else -> "${uiState.reminderMinutesBefore} minutes before"
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
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

        // BUTTONS
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
                    containerColor = Color.White,
                    contentColor = AppGreen
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF9DD8B2)
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
                onClick = {showDeleteConfirmation = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Red
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFFFA5A0)
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
                        containerColor = AppGreen
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
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Delete Appointment")
            },
            text = {
                Text(
                    "Are you sure you want to delete ${uiState.appointmentName} with ${uiState.doctor}?" +
                            "\n\n This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAppointment()
                        showDeleteConfirmation = false
                        onDeleteSuccess()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Red,
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
                        color = TextGrey
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

// DETAIL ROW
@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            tint = DarkText,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.width(22.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 13.sp,
                color = TextGrey
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText
            )
        }
    }
}



// DIVIDER
@Composable
private fun DetailDivider() {

    HorizontalDivider(
        modifier = Modifier.padding(start = 42.dp),
        color = Color(0xFFEDEDED),
        thickness = 1.dp
    )
}



// PREVIEW
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AppointmentDetailsScreenPreview() {

    MaterialTheme {
        AppointmentDetailsScreen(
            appointmentId = 1
        )
    }
}