package com.example.medication_demo.appointment

import androidx.compose.ui.platform.LocalContext
import com.example.medication_demo.reminder.AppointmentReminderScheduler
import com.example.medication_demo.repository.AppointmentRepository
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.EditAppointmentViewModel

private val AppointmentGreen = Color(0xFF16843A)
private val AppointmentBorder = Color(0xFFE1E5E9)
private val AppointmentGrey = Color(0xFF6B7280)
private val AppointmentRed = Color(0xFFC62828)

@Composable
fun EditAppointmentScreen(
    appointmentId: Int,
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: EditAppointmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(appointmentId) {
        viewModel.loadAppointment(appointmentId)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            EditAppointmentTopBar(
                onBackClick = onBackClick
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

            AppointmentTextField(
                value = uiState.doctorName,
                onValueChange = viewModel::updateDoctorName,
                label = "Doctor Name",
                placeholder = "E.g. Dr. James Lee",
                isError = uiState.doctorNameError,
                errorMessage = "Doctor name is required.",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentTextField(
                value = uiState.appointmentName,
                onValueChange = viewModel::updateAppointmentName,
                label = "Appointment Name",
                placeholder = "E.g. Body Check",
                isError = uiState.appointmentNameError,
                errorMessage = "Appointment name is required.",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Appointment Schedule",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppointmentDateField(
                value = uiState.date,
                onValueChange = viewModel::updateDate,
                isError = uiState.dateError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentTimeField(
                value = uiState.time,
                onValueChange = viewModel::updateTime,
                isError = uiState.timeError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            AppointmentReminderField(
                selectedMinutes = uiState.reminderMinutesBefore,
                onReminderSelected = viewModel::updateReminderMinutes
            )

            Spacer(modifier = Modifier.height(18.dp))

            AppointmentTextField(
                value = uiState.location,
                onValueChange = viewModel::updateLocation,
                label = "Hospital / Clinic",
                placeholder = "E.g. City Medical Centre",
                isError = uiState.locationError,
                errorMessage = "Hospital or clinic is required.",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentTextField(
                value = uiState.purpose,
                onValueChange = viewModel::updatePurpose,
                label = "Purpose (Optional)",
                placeholder = "E.g. Regular check-up",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppointmentTextField(
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                label = "Notes (Optional)",
                placeholder = "E.g. Bring previous reports",
                singleLine = false,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(
                color = AppointmentBorder
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (viewModel.saveChanges()) {
                        AppointmentRepository.getAppointmentById(
                            appointmentId
                        )?.let { updatedAppointment ->

                            AppointmentReminderScheduler.cancel(
                                context = context,
                                appointmentId = appointmentId
                            )

                            AppointmentReminderScheduler.schedule(
                                context = context,
                                appointment = updatedAppointment
                            )
                        }
                        onSaveSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppointmentGreen
                )
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EditAppointmentTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(62.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Edit Appointment",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.width(48.dp)
        )
    }
}

