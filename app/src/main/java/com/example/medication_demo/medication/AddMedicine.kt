package com.example.medication_demo.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val EditGreen = Color(0xFF148A32)
private val EditLightGreen = Color(0xFFEAF7ED)
private val EditRed = Color(0xFFFF3B30)
private val EditBorder = Color(0xFFE1E5E9)
private val EditGrey = Color(0xFF6B7280)
private val EditBackground = Color(0xFFFFFFFF)

@Composable
fun AddMedicineScreen(
    isEditMode: Boolean = true,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var medicineName by remember { mutableStateOf("Metformin") }
    var quantity by remember { mutableStateOf("30") }
    var dosageAmount by remember { mutableStateOf("1") }
    var dosageType by remember { mutableStateOf("Tablet") }
    var refillQuantity by remember { mutableStateOf("10") }
    var frequencyAmount by remember { mutableStateOf("Twice") }
    var frequencyPeriod by remember { mutableStateOf("A day") }
    var startDate by remember { mutableStateOf("10 May 2025") }
    var notes by remember { mutableStateOf("Take after meal") }

    val reminderTimes = remember {
        mutableStateListOf(
            ReminderTimeUi("10:00 AM", "6"),
            ReminderTimeUi("08:00 PM", "6")
        )
    }

    Scaffold(
        containerColor = EditBackground,
        topBar = {
            AddMedicineTopBar(
                title = if (isEditMode) "Edit Medicine" else "Add Medicine",
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
            Spacer(modifier = Modifier.height(10.dp))

            // Medicine name and quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                FormTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = "Medicine Name",
                    modifier = Modifier.weight(1.6f)
                )

                DropdownLikeField(
                    label = "Quantity",
                    value = quantity,
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            SectionLabel(text = "Dosage")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownLikeBox(
                    value = dosageAmount,
                    modifier = Modifier.weight(0.8f)
                )

                DropdownLikeBox(
                    value = dosageType,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel(text = "Refill Reminder")

                Spacer(modifier = Modifier.size(6.dp))

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Refill reminder information",
                    tint = EditGreen,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notify when remaining",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.size(12.dp))

                DropdownLikeBox(
                    value = refillQuantity,
                    modifier = Modifier.size(width = 74.dp, height = 52.dp)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "tablets",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionLabel(text = "Frequency")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownLikeBox(
                    value = frequencyAmount,
                    modifier = Modifier.weight(1f)
                )

                DropdownLikeBox(
                    value = frequencyPeriod,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            ReminderHeader()

            Spacer(modifier = Modifier.height(8.dp))

            reminderTimes.forEachIndexed { index, reminder ->
                ReminderTimeRow(
                    time = reminder.time,
                    minutes = reminder.minutes,
                    onTimeChange = { newTime ->
                        reminderTimes[index] = reminder.copy(time = newTime)
                    },
                    onMinutesChange = { newMinutes ->
                        reminderTimes[index] = reminder.copy(minutes = newMinutes)
                    },
                    onRemoveClick = {
                        if (reminderTimes.size > 1) {
                            reminderTimes.removeAt(index)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            TextButton(
                onClick = {
                    reminderTimes.add(
                        ReminderTimeUi(
                            time = "09:00 AM",
                            minutes = "6"
                        )
                    )
                },
                contentPadding = ButtonDefaults.TextButtonContentPadding
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = EditGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(7.dp))

                Text(
                    text = "Add Another Time",
                    style = MaterialTheme.typography.labelLarge,
                    color = EditGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = EditBorder)

            Spacer(modifier = Modifier.height(18.dp))

            DateField(
                label = "Start Date",
                value = startDate,
                onValueChange = { startDate = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (Optional)",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(color = EditBorder)

            Spacer(modifier = Modifier.height(18.dp))

            if (isEditMode) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = EditRed
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EditRed
                    )
                ) {
                    Text(
                        text = "Delete Medicine",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditGreen
                )
            ) {
                Text(
                    text = if (isEditMode) {
                        "Update Medicine"
                    } else {
                        "Save Medicine"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Extra bottom space so the last button is not too close
            // to the phone navigation bar.
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AddMedicineTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 6.dp,
                end = 6.dp,
                top = 28.dp,
                bottom = 6.dp
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
            text = title,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = {},
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
private fun SectionLabel(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EditGrey
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditGreen,
                unfocusedBorderColor = EditBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun DropdownLikeField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EditGrey
        )

        Spacer(modifier = Modifier.height(6.dp))

        DropdownLikeBox(
            value = value,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DropdownLikeBox(
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = EditBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                // Dropdown function will be added later.
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = "Open options",
            tint = EditGrey,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun ReminderHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Time",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = "Remind",
            modifier = Modifier.weight(1.1f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun ReminderTimeRow(
    time: String,
    minutes: String,
    onTimeChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = time,
            onValueChange = onTimeChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditGreen,
                unfocusedBorderColor = EditBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 1.dp,
                    color = EditRed,
                    shape = CircleShape
                )
                .clickable(onClick = onRemoveClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Remove time",
                tint = EditRed,
                modifier = Modifier.size(15.dp)
            )
        }

        Text(
            text = "Every",
            style = MaterialTheme.typography.bodySmall
        )

        DropdownLikeBox(
            value = minutes,
            modifier = Modifier.size(
                width = 64.dp,
                height = 52.dp
            )
        )

        Text(
            text = "min",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EditGrey
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                IconButton(
                    onClick = {
                        // DatePicker will be added later.
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select date",
                        tint = EditGrey
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditGreen,
                unfocusedBorderColor = EditBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

private data class ReminderTimeUi(
    val time: String,
    val minutes: String
)

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AddMedicineScreenPreview() {
    Medication_DemoTheme {
        AddMedicineScreen(
            isEditMode = true
        )
    }
}
