package com.example.medication_demo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalTime

private val PickerGreen = Color(0xFF159447)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    validateTime: ((LocalTime) -> String?)? = null
) {
    val timePickerState =
        rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = false
        )

    var isInputMode by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isInputMode) {
                        TimeInput(
                            state = timePickerState,
                            colors = medicationTimePickerColors()
                        )
                    } else {
                        TimePicker(
                            state = timePickerState,
                            colors = medicationTimePickerColors()
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isInputMode = !isInputMode
                            errorMessage = null
                        }
                    ) {
                        Icon(
                            imageVector =
                                if (isInputMode) {
                                    Icons.Default.Schedule
                                } else {
                                    Icons.Default.Keyboard
                                },
                            contentDescription =
                                if (isInputMode) {
                                    "Use clock"
                                } else {
                                    "Enter time"
                                },
                            tint = PickerGreen
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Cancel",
                            color = PickerGreen
                        )
                    }

                    TextButton(
                        onClick = {
                            val selectedTime =
                                LocalTime.of(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )

                            val error =
                                validateTime?.invoke(selectedTime)

                            if (error != null) {
                                errorMessage = error
                            } else {
                                errorMessage = null
                                onConfirm(selectedTime)
                            }
                        }
                    ) {
                        Text(
                            text = "Done",
                            color = PickerGreen
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun medicationTimePickerColors() =
    TimePickerDefaults.colors(
        clockDialColor =
            PickerGreen.copy(alpha = 0.10f),

        selectorColor =
            PickerGreen,

        clockDialSelectedContentColor =
            Color.White,

        clockDialUnselectedContentColor =
            Color.DarkGray,

        periodSelectorBorderColor =
            PickerGreen,

        periodSelectorSelectedContainerColor =
            PickerGreen,

        periodSelectorSelectedContentColor =
            Color.White,

        periodSelectorUnselectedContainerColor =
            Color.White,

        periodSelectorUnselectedContentColor =
            PickerGreen,

        timeSelectorSelectedContainerColor =
            PickerGreen.copy(alpha = 0.15f),

        timeSelectorSelectedContentColor =
            PickerGreen
    )