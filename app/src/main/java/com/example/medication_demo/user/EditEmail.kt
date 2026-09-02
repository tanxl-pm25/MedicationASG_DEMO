package com.example.medication_demo.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val EditGreen = Color(0xFF159447)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailScreen(
    currentEmail: String,
    onBackClick: () -> Unit = {},
    onSaveClick: (newEmail: String) -> Unit = {}
) {
    var email by remember { mutableStateOf(currentEmail) }
    var submitAttempted by remember { mutableStateOf(false) }

    val emailHasError = submitAttempted &&
            (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())

    val errorMessage = when {
        !submitAttempted -> " "
        email.isBlank() -> "Email cannot be empty"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "Please enter a valid email"
        else -> " "
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Email")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text("Email")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = emailHasError,
                supportingText = {
                    Text(errorMessage)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = {
                    submitAttempted = true

                    val isValidEmail =
                        email.isNotBlank() &&
                                android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(email)
                                    .matches()

                    if (isValidEmail) {
                        onSaveClick(email.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditGreen
                )
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditEmailScreenPreview() {
    Medication_DemoTheme {
        EditEmailScreen(
            currentEmail = ""
        )
    }
}