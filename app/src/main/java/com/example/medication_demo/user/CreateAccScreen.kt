package com.example.medication_demo.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Patterns
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val SignUpGreen = Color(0xFF159447)

private fun isValidSignUpEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onSignUpClick: (name: String, email: String, password: String, confirmPassword: String) -> Unit = { _, _, _, _ -> },
    onGoogleClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val userNameHasError = submitAttempted && userName.isBlank()
    val emailHasError = submitAttempted && (email.isBlank() || !isValidSignUpEmail(email))
    val passwordHasError = submitAttempted && (password.isBlank() || password.length < 6)
    val confirmPasswordHasError = submitAttempted &&
            (confirmPassword.isBlank() || confirmPassword != password)
    val termsHasError = submitAttempted && !agreedToTerms

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heart_logo),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Let's get you started!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Username") },
                placeholder = { Text("Enter your username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null
                    ) },
                singleLine = true,
                isError = userNameHasError,
                supportingText = {
                    if (userNameHasError) {
                        Text("Username cannot be empty")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("Enter your email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null
                    ) },
                singleLine = true,
                isError = emailHasError || errorMessage != null,
                supportingText = {
                    if (emailHasError) {
                        Text(
                            text = if (email.isBlank()) {
                                "Email cannot be empty"
                            } else {
                                "Please enter a valid email address"
                            }
                        )
                    } else if (errorMessage != null) {
                        Text(errorMessage)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Create a password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null
                    ) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = passwordHasError,
                supportingText = {
                    if (passwordHasError) {
                        Text(
                            text = if (password.isBlank()) {
                                "Password cannot be empty"
                            } else {
                                "Password must be at least 6 characters"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Confirm your password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null
                    ) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                isError = confirmPasswordHasError,
                supportingText = {
                    if (confirmPasswordHasError) {
                        Text(
                            text = if (confirmPassword.isBlank()) {
                                "Please confirm your password"
                            } else {
                                "Passwords do not match"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (termsHasError) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 2.5.dp,
                                    color = MaterialTheme.colorScheme.error,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SignUpGreen,
                            uncheckedColor = if (termsHasError) {
                                Color.Transparent
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }
                val termsAnnotatedText = buildAnnotatedString {
                    val linkColor = if (termsHasError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        SignUpGreen
                    }
                    append("I agree to the ")
                    withLink(
                        LinkAnnotation.Clickable(tag = "TERMS") {
                            showTermsDialog = true
                        }
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = linkColor,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Terms of Service")
                        }
                    }
                    append(" and ")
                    withLink(
                        LinkAnnotation.Clickable(tag = "PRIVACY") {
                            showPrivacyDialog = true
                        }
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = linkColor,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline)
                        ) {
                            append("Privacy Policy")
                        }
                    }
                }

                Text(
                    text = termsAnnotatedText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (termsHasError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color.Unspecified
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sign Up button
            Button(
                onClick = {
                    submitAttempted = true
                    if (userName.isNotBlank() &&
                        email.isNotBlank() && isValidSignUpEmail(email) &&
                        password.isNotBlank() && password.length >= 6 &&
                        confirmPassword == password &&
                        agreedToTerms
                    ) {
                        onSignUpClick(userName, email, password, confirmPassword)
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF159447))
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "or continue with" divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "or continue with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(18.dp))

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google button
            OutlinedButton(
                onClick = onGoogleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = null,
                        modifier = Modifier.height(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Continue with Google",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onGoogleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = null,
                        modifier = Modifier.height(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Continue with Facebook",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Already have an account? Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SignUpGreen,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            shape = RoundedCornerShape(4.dp),
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Last updated: September 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TermsSection(
                        title = "1. Acceptance of Terms",
                        body = "By creating an account and using Smart " +
                                "Medication Companion (\"the App\"), you agree to " +
                                "be bound by these Terms of Service. If you do not " +
                                "agree with any part of these terms, please do not " +
                                "use the App."
                    )

                    TermsSection(
                        title = "2. Description of Service",
                        body = "Smart Medication Companion helps you manage " +
                                "your medication schedule, set reminders, log " +
                                "when you take your medicine, and keep a history " +
                                "of your medication activity."
                    )

                    TermsSection(
                        title = "3. Medical Disclaimer",
                        body = "The App is not a substitute for professional " +
                                "medical advice, diagnosis, or treatment. Always " +
                                "consult your doctor, pharmacist, or another " +
                                "qualified healthcare provider before starting, " +
                                "stopping, or changing any medication. Never " +
                                "disregard professional medical advice because of " +
                                "something you read or a reminder you received in " +
                                "this App.\n\n" +
                                "If you experience any unusual symptoms, side " +
                                "effects, or a change in your physical condition, " +
                                "seek medical attention promptly. In case of a " +
                                "medical emergency, contact your local emergency " +
                                "services immediately instead of relying on this " +
                                "App."
                    )

                    TermsSection(
                        title = "4. User Accounts",
                        body = "You are responsible for maintaining the " +
                                "confidentiality of your account credentials and " +
                                "for all activities that occur under your account. " +
                                "You agree to provide accurate and complete " +
                                "information when creating your account."
                    )

                    TermsSection(
                        title = "5. Reminders and Notifications",
                        body = "While the App is designed to send timely " +
                                "medication reminders, we do not guarantee that " +
                                "notifications will always be delivered on time or " +
                                "at all, due to factors such as device settings, " +
                                "network connectivity, or operating system " +
                                "limitations. You remain responsible for taking " +
                                "your medication as prescribed."
                    )

                    TermsSection(
                        title = "6. User Conduct",
                        body = "You agree not to misuse the App, including " +
                                "but not limited to attempting to access accounts " +
                                "that are not yours, interfering with the App's " +
                                "normal operation, or using the App for any " +
                                "unlawful purpose."
                    )

                    TermsSection(
                        title = "7. Limitation of Liability",
                        body = "To the fullest extent permitted by law, Smart " +
                                "Medication Companion and its developers shall not " +
                                "be liable for any missed doses, health outcomes, " +
                                "or other damages arising from your use of, or " +
                                "inability to use, the App."
                    )

                    TermsSection(
                        title = "8. Changes to These Terms",
                        body = "We may update these Terms of Service from " +
                                "time to time. Continued use of the App after any " +
                                "changes constitutes your acceptance of the " +
                                "revised terms."
                    )

                    TermsSection(
                        title = "9. Contact Us",
                        body = "If you have any questions about these Terms " +
                                "of Service, please contact us through the Help " +
                                "& Support section in the App."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close", color = SignUpGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            shape = RoundedCornerShape(4.dp),
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Last updated: September 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TermsSection(
                        title = "1. Introduction",
                        body = "This Privacy Policy explains how Smart " +
                                "Medication Companion (\"the App\") collects, " +
                                "uses, and protects your information when you use " +
                                "our services."
                    )

                    TermsSection(
                        title = "2. Information We Collect",
                        body = "We collect information you provide directly, " +
                                "such as your name, email address, gender, and " +
                                "age. We also collect the medication information " +
                                "you enter, including medicine names, dosages, " +
                                "schedules, and your medication-taking history."
                    )

                    TermsSection(
                        title = "3. Camera and Photo Access",
                        body = "If you choose to set a profile photo, the App " +
                                "may request access to your device's camera or " +
                                "photo library. This access is only used to let " +
                                "you take or select a photo for your profile, and " +
                                "the photo is not accessed for any other purpose. " +
                                "You may deny this permission and continue using " +
                                "the App without a profile photo."
                    )

                    TermsSection(
                        title = "4. How We Use Your Information",
                        body = "Your information is used to personalize your " +
                                "experience, send medication reminders, display " +
                                "your medication history and statistics, and " +
                                "improve the overall functionality of the App."
                    )

                    TermsSection(
                        title = "5. Data Storage and Security",
                        body = "Your data is stored securely using our " +
                                "backend service provider (Supabase). We take " +
                                "reasonable measures to protect your information " +
                                "from unauthorized access, alteration, or " +
                                "disclosure."
                    )

                    TermsSection(
                        title = "6. Sharing of Information",
                        body = "We do not sell, rent, or share your personal " +
                                "or health information with third parties for " +
                                "marketing purposes. Your information is only " +
                                "used to power features within the App."
                    )

                    TermsSection(
                        title = "7. Your Rights",
                        body = "You may access, update, or delete your " +
                                "personal information at any time through your " +
                                "Profile settings. You may also request full " +
                                "account deletion by contacting us."
                    )

                    TermsSection(
                        title = "8. Children's Privacy",
                        body = "The App is not intended for children under " +
                                "the age of 13. We do not knowingly collect " +
                                "personal information from children under 13."
                    )

                    TermsSection(
                        title = "9. Changes to This Policy",
                        body = "We may update this Privacy Policy from time " +
                                "to time. We encourage you to review this page " +
                                "periodically for any changes."
                    )

                    TermsSection(
                        title = "10. Contact Us",
                        body = "If you have any questions about this Privacy " +
                                "Policy, please contact us through the Help & " +
                                "Support section in the App."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = SignUpGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun TermsSection(
    title: String,
    body: String
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateAccountScreenPreview() {
    Medication_DemoTheme {
        CreateAccountScreen()
    }
}