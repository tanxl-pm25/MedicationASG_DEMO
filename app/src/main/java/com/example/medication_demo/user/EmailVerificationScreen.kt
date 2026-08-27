package com.example.medication_demo.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import kotlinx.coroutines.delay

private val VerifyGreen = Color(0xFF159447)

@Composable
fun EmailVerificationScreen(
    email: String = "example@gmail.com",
    onVerifyClick: (code: String) -> Unit = {},
    onResendClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var digits by remember { mutableStateOf(List(6) { "" }) }

    // time countdown
    var secondsLeft by remember { mutableIntStateOf(60) }

    // 1s - 1 until 0
    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We have sent a 6-digit code to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerifyGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 6 个数字输入框
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    digits.forEachIndexed { index, digit ->
                        OutlinedTextField(
                            value = digit,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    digits = digits.toMutableList().also { it[index] = newValue }
                                }
                            },
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (secondsLeft > 0) {
                        "Resend code in 00:${secondsLeft.toString().padStart(2, '0')}"
                    } else {
                        "Didn't get it? Resend code"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (secondsLeft > 0) MaterialTheme.colorScheme.onSurfaceVariant else VerifyGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.email_verification),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onVerifyClick(digits.joinToString("")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerifyGreen)
            ) {
                Text("Verify", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmailVerificationScreenPreview() {
    Medication_DemoTheme {
        EmailVerificationScreen()
    }
}