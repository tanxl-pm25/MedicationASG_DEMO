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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

private val VerifyGreen = Color(0xFF159447)

@Composable
fun EmailVerificationScreen(
    email: String = "example@gmail.com",
    onVerifyClick: (code: String) -> Unit = {},
    onResendClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var digits by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6){ FocusRequester() } }

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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "We have sent a 6-digit code to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerifyGreen
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 6 Box
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    digits.forEachIndexed { index, digit ->
                        OutlinedTextField(
                            value = digit,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    digits = digits.toMutableList().also { it[index] = newValue }

                                    if (newValue.isNotEmpty() && index < 5){
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .focusRequester(focusRequesters[index]),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(35.dp))

                if (secondsLeft > 0) {
                    Text(
                        text = buildAnnotatedString {
                            append("Resend code in ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("00:${secondsLeft.toString().padStart(2, '0')}")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val annotatedText = buildAnnotatedString {
                        append("Didn't get it? ")
                        pushStringAnnotation(
                            tag = "RESEND",
                            annotation = "resend"
                        )
                        withStyle(
                            style = SpanStyle(color = VerifyGreen, fontWeight = FontWeight.Bold)
                        ) {
                            append("Resend code")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyMedium,
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(
                                tag = "RESEND",
                                start = offset,
                                end = offset
                            )
                                .firstOrNull()?.let {
                                    onResendClick()
                                    secondsLeft = 60
                                }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.verification),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onVerifyClick(digits.joinToString("")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerifyGreen)
            ) {
                Text(
                    text = "Verify",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.labelLarge
                )
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