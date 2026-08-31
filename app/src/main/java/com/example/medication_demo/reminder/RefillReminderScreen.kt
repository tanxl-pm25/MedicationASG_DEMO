package com.example.medication_demo.reminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val ReminderGreen = Color(0xFF159447)
private val ReminderBackground = Color.White
private val ReminderTextGrey = Color(0xFF555555)

@Composable
fun RefillReminderScreen(
    medicineName: String = "Metformin",
    tabletsLeft: Int = 5,
    onRefillConfirm: (Int) -> Unit = {},
    onRemindAgainConfirm: (Int) -> Unit = {}
) {
    var showRefillDialog by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }
    var showRemindAgainDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = ReminderBackground
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "Low medicine quantity reminder",
                    modifier = Modifier.size(190.dp)
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Running Low!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Text(
                    text =
                        "You have $tabletsLeft tablets left of\n$medicineName.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReminderTextGrey,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text =
                        "It’s time to refill your\n$medicineName.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReminderTextGrey,
                    textAlign = TextAlign.Center
                )

                if (message != null) {
                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text = message!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = ReminderGreen,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(
                    modifier = Modifier.height(30.dp)
                )

                Button(
                    onClick = {
                        showRefillDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReminderGreen
                    )
                ) {
                    Text(
                        text = "Refill",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                TextButton(
                    onClick = {
                        showRemindAgainDialog = true
                    }
                ) {
                    Text(
                        text = "Remind Again",
                        style = MaterialTheme.typography.labelLarge,
                        color = ReminderGreen
                    )
                }
            }
        }
    }

    if (showRefillDialog) {
        RefillQuantityDialog(
            medicineName = medicineName,

            onDismiss = {
                showRefillDialog = false
            },

            onConfirm = { newQuantity ->
                showRefillDialog = false

                message =
                    "$medicineName has been refilled."

                onRefillConfirm(newQuantity)
            }
        )
    }

    if (showRemindAgainDialog) {
        RemindAgainDialog(
            onDismiss = {
                showRemindAgainDialog = false
            },

            onConfirm = { minutes ->
                showRemindAgainDialog = false
                onRemindAgainConfirm(minutes)
            }
        )
    }
}


@Composable
private fun RefillQuantityDialog(
    medicineName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantityText by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Refill $medicineName"
            )
        },

        text = {
            Column {
                Text(
                    text = "Enter the new medicine quantity."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = quantityText,

                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            quantityText = value
                            errorMessage = null
                        }
                    },

                    label = {
                        Text("New Quantity")
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),

                    isError =
                        errorMessage != null,

                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!
                            )
                        }
                    }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val quantity =
                        quantityText.toIntOrNull()

                    if (
                        quantity == null ||
                        quantity <= 0
                    ) {
                        errorMessage =
                            "Please enter a quantity greater than 0."
                    } else {
                        onConfirm(quantity)
                    }
                }
            ) {
                Text(
                    text = "Confirm",
                    color = ReminderGreen
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}

@Composable
private fun RemindAgainDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var minutesText by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Remind Again"
            )
        },

        text = {
            Column {
                Text(
                    text = "How many minutes later would you like to be reminded?"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = minutesText,

                    onValueChange = { value ->
                        if (value.all { it.isDigit() }) {
                            minutesText = value
                            errorMessage = null
                        }
                    },

                    label = {
                        Text("Minutes")
                    },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    isError = errorMessage != null,

                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!
                            )
                        }
                    }
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val minutes =
                        minutesText.toIntOrNull()

                    if (
                        minutes == null ||
                        minutes <= 0
                    ) {
                        errorMessage =
                            "Please enter a number greater than 0."
                    } else {
                        onConfirm(minutes)
                    }
                }
            ) {
                Text(
                    text = "Confirm",
                    color = ReminderGreen
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RefillReminderScreenPreview() {
    Medication_DemoTheme {
        RefillReminderScreen()
    }
}