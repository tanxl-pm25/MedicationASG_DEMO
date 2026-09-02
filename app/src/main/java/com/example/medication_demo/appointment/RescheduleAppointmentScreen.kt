package com.example.medication_demo.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.RescheduleAppointmentViewModel

private val RescheduleGreen = Color(0xFF168A45)
private val RescheduleDarkText = Color(0xFF1E293B)
private val RescheduleTextGrey = Color(0xFF64748B)
private val RescheduleLightGreen = Color(0xFFEAF7EE)

@Composable
fun RescheduleAppointmentScreen(
    appointmentId: Int,
    onBackClick: () -> Unit,
    onRescheduleSuccess: () -> Unit,
    viewModel: RescheduleAppointmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointment(appointmentId)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = RescheduleDarkText
                    )
                }

                Text(
                    text = "Reschedule Appointment",
                    modifier = Modifier.weight(1f),
                    textAlign =
                        androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RescheduleDarkText
                )

                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    ) { innerPadding ->

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = RescheduleGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Choose a new time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RescheduleDarkText
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your previous appointment will remain in History.",
                    fontSize = 14.sp,
                    color = RescheduleTextGrey
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = RescheduleLightGreen
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.doctor,
                            fontWeight = FontWeight.Bold,
                            color = RescheduleDarkText
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = uiState.appointmentName,
                            fontSize = 14.sp,
                            color = RescheduleTextGrey
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = RescheduleGreen
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = uiState.location,
                                fontSize = 14.sp,
                                color = RescheduleDarkText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AppointmentDateField(
                    value = uiState.newDate,
                    onValueChange = viewModel::updateDate,
                    isError = uiState.dateError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppointmentTimeField(
                    value = uiState.newTime,
                    onValueChange = viewModel::updateTime,
                    isError = uiState.timeError,
                    modifier = Modifier.fillMaxWidth()

                )

                uiState.scheduleError?.let { message ->
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = message,
                        color = Color(0xFFE53935),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                AppointmentReminderField(
                    selectedMinutes =
                        uiState.reminderMinutesBefore,
                    onReminderSelected =
                        viewModel::updateReminderMinutes
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (
                            viewModel.rescheduleAppointment()
                        ) {
                            onRescheduleSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RescheduleGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Confirm Reschedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}