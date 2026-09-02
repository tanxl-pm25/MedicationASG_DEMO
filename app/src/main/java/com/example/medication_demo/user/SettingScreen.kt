package com.example.medication_demo.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val SettingGreen = Color(0xFF159447)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    selectedLanguage: String = "English",
    isDarkMode: Boolean = false,
    isGoogleLinked: Boolean = false,
    linkedGoogleEmail: String? = null,
    isFacebookLinked: Boolean = false,
    linkedFacebookName: String? = null,
    onBackClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {},
    onDarkModeChange: (Boolean) -> Unit = {},
    onOpenCameraPermissionSettings: () -> Unit = {},
    onOpenPhotosPermissionSettings: () -> Unit = {},
    onOpenNotificationPermissionSettings: () -> Unit = {},
    onLinkGoogleClick: () -> Unit = {},
    onUnlinkGoogleClick: () -> Unit = {},
    onLinkFacebookClick: () -> Unit = {},
    onUnlinkFacebookClick: () -> Unit = {}
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showUnlinkGoogleDialog by remember { mutableStateOf(false) }
    var showUnlinkFacebookDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Linked Accounts
            SettingSectionHeader(title = "Linked Accounts")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    SettingAccountRow(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.google_icon),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = "Google Account",
                        isLinked = isGoogleLinked,
                        linkedInfo = linkedGoogleEmail,
                        onLinkClick = onLinkGoogleClick,
                        onUnlinkClick = { showUnlinkGoogleDialog = true }
                    )
                    SettingDivider()
                    SettingAccountRow(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.age_cake_pic),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = "Facebook Account",
                        isLinked = isFacebookLinked,
                        linkedInfo = linkedFacebookName,
                        onLinkClick = onLinkFacebookClick,
                        onUnlinkClick = { showUnlinkFacebookDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Permissions
            SettingSectionHeader(title = "Permissions")
            Text(
                text = "These open your device's system settings for this app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    SettingClickableRow(
                        icon = Icons.Filled.CameraAlt,
                        label = "Camera",
                        value = "System settings",
                        onClick = onOpenCameraPermissionSettings
                    )
                    SettingDivider()
                    SettingClickableRow(
                        icon = Icons.Filled.Photo,
                        label = "Photos",
                        value = "System settings",
                        onClick = onOpenPhotosPermissionSettings
                    )
                    SettingDivider()
                    SettingClickableRow(
                        icon = Icons.Filled.NotificationsNone,
                        label = "Notifications",
                        value = "System settings",
                        onClick = onOpenNotificationPermissionSettings
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences
            SettingSectionHeader(title = "Preferences")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    SettingClickableRow(
                        icon = Icons.Filled.Language,
                        label = "Language",
                        value = selectedLanguage,
                        onClick = { showLanguageDialog = true }
                    )
                    SettingDivider()
                    SettingSwitchRow(
                        icon = Icons.Filled.DarkMode,
                        label = "Dark Mode",
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeChange
                    )
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = selectedLanguage,
            onDismiss = { showLanguageDialog = false },
            onConfirm = { language ->
                onLanguageChange(language)
                showLanguageDialog = false
            }
        )
    }

    if (showUnlinkGoogleDialog) {
        UnlinkConfirmDialog(
            accountName = "Google",
            onDismiss = { showUnlinkGoogleDialog = false },
            onConfirm = {
                showUnlinkGoogleDialog = false
                onUnlinkGoogleClick()
            }
        )
    }

    if (showUnlinkFacebookDialog) {
        UnlinkConfirmDialog(
            accountName = "Facebook",
            onDismiss = { showUnlinkFacebookDialog = false },
            onConfirm = {
                showUnlinkFacebookDialog = false
                onUnlinkFacebookClick()
            }
        )
    }
}

@Composable
private fun UnlinkConfirmDialog(
    accountName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(10.dp),
        title = {
            Text(
                text = "Unlink $accountName account?",
                fontWeight = FontWeight.Bold
            )},
        text = {
            Text("You'll need to link it again if you want to use it to sign in.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Unlink",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp, top = 4.dp)
    )
}

@Composable
private fun SettingAccountRow(
    icon: @Composable () -> Unit,
    label: String,
    isLinked: Boolean,
    linkedInfo: String?,
    onLinkClick: () -> Unit,
    onUnlinkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (isLinked) {
                    linkedInfo ?: "Linked"
                } else {
                    "Not linked"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isLinked) {
            OutlinedButton(onClick = onUnlinkClick) {
                Icon(
                    imageVector = Icons.Filled.LinkOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Unlink")
            }
        } else {
            OutlinedButton(onClick = onLinkClick) {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Link")
            }
        }
    }
}

@Composable
private fun SettingClickableRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SettingGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SettingGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = SettingGreen)
        )
    }
}

@Composable
private fun SettingDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color(0xFFE5E5E5)
    )
}

@Composable
private fun LanguagePickerDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val languages = listOf("English", "中文 (Chinese)", "Bahasa Malaysia")
    var selected by remember { mutableStateOf(currentLanguage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(10.dp),
        title = {
            Text(
                text = "Choose Language",
                fontWeight = FontWeight.Bold
            ) },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = language == selected,
                                onClick = { selected = language },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == selected,
                            onClick = null,
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = SettingGreen
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(language)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("Confirm", color = SettingGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingScreenPreview() {
    Medication_DemoTheme {
        SettingScreen(
            selectedLanguage = "English",
            isDarkMode = false,
            isGoogleLinked = true,
            linkedGoogleEmail = "qiuyutan23@gmail.com",
            isFacebookLinked = false
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UnlinkConfirmDialogPreview() {
    Medication_DemoTheme {
        UnlinkConfirmDialog(
            accountName = "Google",
            onDismiss = {},
            onConfirm = {}
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LanguagePickerDialogPreview() {
    Medication_DemoTheme {
        LanguagePickerDialog(
            currentLanguage = "English",
            onDismiss = {},
            onConfirm = {}
        )
    }
}