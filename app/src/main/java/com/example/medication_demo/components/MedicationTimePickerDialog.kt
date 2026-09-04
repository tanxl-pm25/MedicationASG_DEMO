package com.example.medication_demo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    validateTime: ((LocalTime) -> String?)? = null
) {
    val timePickerState = rememberTimePickerState(
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
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
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

                errorMessage?.let { message ->
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = message,
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
                            tint = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = {
                            val selectedTime = LocalTime.of(
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
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun medicationTimePickerColors(): TimePickerColors {
    val colorScheme = MaterialTheme.colorScheme

    return TimePickerDefaults.colors(
        clockDialColor =
            colorScheme.primary.copy(alpha = 0.12f),

        selectorColor =
            colorScheme.primary,

        clockDialSelectedContentColor =
            colorScheme.onPrimary,

        clockDialUnselectedContentColor =
            colorScheme.onSurface,

        periodSelectorBorderColor =
            colorScheme.outline,

        periodSelectorSelectedContainerColor =
            colorScheme.primary,

        periodSelectorSelectedContentColor =
            colorScheme.onPrimary,

        periodSelectorUnselectedContainerColor =
            colorScheme.surfaceVariant,

        periodSelectorUnselectedContentColor =
            colorScheme.onSurfaceVariant,

        timeSelectorSelectedContainerColor =
            colorScheme.primary.copy(alpha = 0.16f),

        timeSelectorSelectedContentColor =
            colorScheme.primary
    )
}