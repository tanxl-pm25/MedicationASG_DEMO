package com.example.medication_demo.appointment

import androidx.compose.material3.SelectableDates
import com.example.medication_demo.utils.getMalaysiaDate
import androidx.compose.ui.platform.LocalContext
import com.example.medication_demo.reminder.AppointmentReminderScheduler
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import com.example.medication_demo.components.MedicationTimePickerDialog
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.AddAppointmentViewModel
import java.util.Locale

@Composable
fun AddAppointmentScreen(
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    viewModel: AddAppointmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddAppointmentTopBar(
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

            uiState.scheduleError?.let { message ->
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

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
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    val savedAppointment =
                        viewModel.saveAppointment()

                    if (savedAppointment != null) {
                        AppointmentReminderScheduler.schedule(
                            context = context,
                            appointment = savedAppointment
                        )

                        onSaveSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Save Appointment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddAppointmentTopBar(
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
            text = "Add Appointment",
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

@Composable
fun AppointmentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            enabled = true,
            readOnly = false,
            keyboardOptions = keyboardOptions,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            )
        )

        if (isError && errorMessage != null) {
            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDateField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        Text(
            text = "Date",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = isError,
                placeholder = {
                    Text("Select date")
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select date",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        showDatePicker = true
                    }
            )
        }

        if (isError) {
            Text(
                text = "Date is required.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }

    if (showDatePicker) {
        val today = getMalaysiaDate()

        val selectableDates = remember(today) {
            object : SelectableDates {
                override fun isSelectableDate(
                    utcTimeMillis: Long
                ): Boolean {
                    val date = Instant
                        .ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    return !date.isBefore(today)
                }

                override fun isSelectableYear(
                    year: Int
                ): Boolean {
                    return year >= today.year
                }
            }
        }

        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy",
            Locale.ENGLISH
        )

        val parsedDate = try {
            LocalDate.parse(value, formatter)
        } catch (_: Exception) {
            today
        }

        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = parsedDate
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
            selectableDates = selectableDates
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            onValueChange(
                                selectedDate.format(formatter)
                            )

                            showDatePicker = false
                        }
                    }
                ) {
                    Text("Done", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun AppointmentTimeField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember {
        mutableStateOf(false)
    }

    val formatter = DateTimeFormatter.ofPattern(
        "hh:mm a",
        Locale.ENGLISH
    )

    val parsedTime = try {
        LocalTime.parse(
            value.uppercase(Locale.ENGLISH),
            formatter
        )
    } catch (_: Exception) {
        LocalTime.of(9, 0)
    }

    Column(modifier = modifier) {
        Text(
            text = "Time",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                isError = isError,
                placeholder = {
                    Text("Select time")
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Select time",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        showTimePicker = true
                    }
            )
        }

        if (isError) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Time is required.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }

    if (showTimePicker) {
        MedicationTimePickerDialog(
            initialTime = parsedTime,
            onDismiss = {
                showTimePicker = false
            },
            onConfirm = { selectedTime ->
                onValueChange(
                    selectedTime.format(formatter)
                )

                showTimePicker = false
            }
        )
    }
}

@Composable
fun AppointmentReminderField(
    selectedMinutes: Int?,
    onReminderSelected: (Int?) -> Unit
) {
    val options = listOf(
        "At appointment time" to 0,
        "5 minutes before" to 5,
        "15 minutes before" to 15,
        "30 minutes before" to 30,
        "1 hour before" to 60,
        "1 day before" to 1440,
        "No reminder" to null
    )

    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = options.firstOrNull {
        it.second == selectedMinutes
    }?.first ?: "Select reminder"

    Column {
        Text(
            text = "Reminder",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box (
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = selectedLabel,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Select reminder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option.first)
                        },
                        onClick = {
                            onReminderSelected(option.second)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}