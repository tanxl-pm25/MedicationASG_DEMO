package com.example.medication_demo.medication

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medication_demo.viewmodel.MedicineViewModel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import androidx.compose.material3.RadioButton
import androidx.compose.ui.res.painterResource
import com.example.medication_demo.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.components.MedicationTimePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.medication_demo.ui.AppTopBar

private val EditGreen = Color(0xFF148A32)
private val EditRed = Color(0xFFFF3B30)
private val EditBorder = Color(0xFFE1E5E9)
private val EditGrey = Color(0xFF6B7280)
private val EditBackground = Color(0xFFFFFFFF)

@Composable
fun AddMedicineScreen(
    isEditMode: Boolean = true,
    medicineId: Int? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    vm: MedicineViewModel = viewModel()
) {
    val medicineName by vm.medicineName.collectAsStateWithLifecycle()
    val quantity by vm.quantity.collectAsStateWithLifecycle()
    val dosageAmount by vm.dosageAmount.collectAsStateWithLifecycle()
    val dosageType by vm.dosageType.collectAsStateWithLifecycle()
    val refillQuantity by vm.refillQuantity.collectAsStateWithLifecycle()
    val frequency by vm.frequency.collectAsStateWithLifecycle()
    val frequencyDraft by vm.frequencyDraft.collectAsStateWithLifecycle()
    val isCustomFrequency by vm.isCustomFrequency.collectAsStateWithLifecycle()
    val customFrequencyNumber by vm.customFrequencyNumber.collectAsStateWithLifecycle()
    val customFrequencyUnit by vm.customFrequencyUnit.collectAsStateWithLifecycle()
    val startDate by vm.startDate.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val reminderTimes by vm.reminderTimes.collectAsStateWithLifecycle()
    val refillReminderEnabled by vm.refillReminderEnabled.collectAsStateWithLifecycle()
    val showFrequencyDialog by vm.showFrequencyDialog.collectAsStateWithLifecycle()
    val requiredReminderTimeCount by vm.requiredReminderTimeCount.collectAsStateWithLifecycle()
    val medicineNameError by vm.medicineNameError.collectAsStateWithLifecycle()
    val quantityError by vm.quantityError.collectAsStateWithLifecycle()
    val dosageAmountError by vm.dosageAmountError.collectAsStateWithLifecycle()
    val refillQuantityError by vm.refillQuantityError.collectAsStateWithLifecycle()
    val customFrequencyError by vm.customFrequencyError.collectAsStateWithLifecycle()
    val reminderTimeError by vm.reminderTimeError.collectAsStateWithLifecycle()
    val presetImageRes by vm.presetImageRes.collectAsStateWithLifecycle()
    val galleryImageUri by vm.galleryImageUri.collectAsStateWithLifecycle()
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                vm.onGalleryImageSelected(
                    uri.toString()
                )
            }
        }
    val context = LocalContext.current
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                vm.onRefillReminderEnabledChange(true)
            }
        }
    var showImageOptions by remember { mutableStateOf(false) }
    var showPresetImages by remember { mutableStateOf(false) }
    var showAsNeededGuide by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = EditBackground,
        topBar = {
            AppTopBar(
                title =
                    if (isEditMode) {
                        "Edit Medicine"
                    } else {
                        "Add Medicine"
                    },

                onBackClick = onBackClick,

                showMoreMenu = true,

                onHelpClick = {
                    showHelpDialog = true
                }
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
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Left ：Medicine Image
                Column(
                    modifier = Modifier.width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    MedicineImageSelector(
                        presetImageRes = presetImageRes,
                        galleryImageUri = galleryImageUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                    )

                    TextButton(
                        onClick = {
                            showImageOptions = true
                        },
                        contentPadding = PaddingValues(
                            horizontal = 4.dp
                        )
                    ) {
                        Text(
                            text = if (
                                presetImageRes != null ||
                                galleryImageUri != null
                            ) {
                                "Change Image"
                            } else {
                                "+ Add Image"
                            },
                            color = EditGreen,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Right：Name + Quantity
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    FormTextField(
                        value = medicineName,
                        onValueChange = vm::onMedicineNameChange,
                        label = "Medicine Name",
                        placeholder = "E.g. Metformin",
                        isError = medicineNameError != null,
                        errorMessage = medicineNameError,
                        modifier = Modifier.fillMaxWidth()
                    )

                    NumberInputField(
                        label = "Quantity",
                        value = quantity,
                        onValueChange = vm::onQuantityChange,
                        placeholder = "E.g. 10",
                        isError = quantityError != null,
                        errorMessage = quantityError,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            SectionLabel(text = "Dosage")
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NumberInputField(
                    value = dosageAmount,
                    onValueChange = vm::onDosageAmountChange,
                    allowDecimal = true,
                    placeholder = "E.g. 1",
                    isError = dosageAmountError != null,
                    errorMessage = dosageAmountError,
                    modifier = Modifier.weight(0.8f)
                )

                DropdownLikeBox(
                    value = dosageType,
                    options = listOf(
                        "Tablet", "Capsule", "Liquid", "Injectable", "Cream", "Gel", "Syrup", "Drop"
                    ),
                    onValueSelected = vm::onDosageTypeChange,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Refill Reminder",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Refill reminder information",
                        tint = EditGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                RefillReminderSwitch(
                    checked = refillReminderEnabled,
                    onCheckedChange = { enabled ->
                        if (!enabled) {
                            // User turns the reminder OFF.
                            vm.onRefillReminderEnabledChange(false)
                        } else {
                            // Android 13 and above require notification permission
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val permissionGranted =
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                if (permissionGranted) {
                                    vm.onRefillReminderEnabledChange(true)
                                } else {
                                    notificationPermissionLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                }
                            } else {
                                // Android 12 and below do not require
                                // POST_NOTIFICATIONS runtime permission.
                                vm.onRefillReminderEnabledChange(true)
                            }
                        }
                    }
                )
            }

            if (refillReminderEnabled) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remind me when only",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EditGrey
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberInputField(
                            value = refillQuantity,
                            onValueChange = vm::onRefillQuantityChange,
                            placeholder = "E.g. 10",
                            isError = refillQuantityError != null,
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "left",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EditGrey
                        )
                    }
                    if (refillQuantityError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = refillQuantityError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = EditRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FrequencyField(
                value = frequency,
                onClick = vm::openFrequencyDialog

            )
            Spacer(modifier = Modifier.height(18.dp))

            if (requiredReminderTimeCount > 0) {
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = EditBorder)
                Spacer(modifier = Modifier.height(8.dp))
                if (requiredReminderTimeCount > 1) {
                    Text(
                        text = "Set $requiredReminderTimeCount reminder times",
                        style = MaterialTheme.typography.bodySmall,
                        color = EditGrey
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                reminderTimes.forEachIndexed { index, reminder ->
                    ReminderTimeRow(
                        time = reminder.time,
                        minutes = reminder.minutes,
                        reminderOptionsEnabled = reminder.reminderOptionsEnabled,
                        minutesError = reminder.minutesError,
                        onTimeChange = { newTime ->
                            vm.updateReminderTime(
                                index = index,
                                newTime = newTime
                            )
                        },
                        onMinutesChange = { newMinutes ->
                            vm.updateReminderMinutes(
                                index = index,
                                newMinutes = newMinutes
                            )
                        },
                        onReminderOptionsClick = { vm.toggleReminderOptions(index) },
                        onRemoveClick = { vm.removeReminderTime(index) }
                    )
                    if (index != reminderTimes.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = EditBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                TextButton(
                    onClick = { vm.addReminderTime() },
                    contentPadding = PaddingValues(
                        horizontal = 0.dp,
                        vertical = 4.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = EditGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Another Time",
                        style = MaterialTheme.typography.labelLarge,
                        color = EditGreen
                    )
                }
                if (reminderTimeError != null) {
                    Text(
                        text = reminderTimeError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = EditRed
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = EditBorder)
                Spacer(modifier = Modifier.height(14.dp))
            }

            DateField(
                label = "Start Date",
                value = startDate,
                onValueChange = vm::onStartDateChange
            )

            Spacer(modifier = Modifier.height(18.dp))

            FormTextField(
                value = notes,
                onValueChange = vm::onNotesChange,
                label = "Notes (Optional)",
                modifier = Modifier.fillMaxWidth(),
                placeholder = "E.g. Take after meal",
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(color = EditBorder)

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    val success =
                        if (isEditMode && medicineId != null) {
                            vm.updateMedicine(
                                id = medicineId
                            )
                        } else {
                            vm.addMedicine()
                        }
                    if (success) {
                        onSaveClick()
                    }
                },
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

            // Extra bottom space so the last button is not too close to the phone navigation bar.
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    if (showHelpDialog) {

        AlertDialog(
            onDismissRequest = {
                showHelpDialog = false
            },

            title = {
                Text("Medicine Help")
            },

            text = {
                Text(
                    "Enter your medicine information, dosage, " +
                            "frequency and reminder settings."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showHelpDialog = false
                    }
                ) {
                    Text("Got it")
                }
            }
        )
    }

    if (showFrequencyDialog) {
        FrequencyDialog(
            frequencyDraft = frequencyDraft,
            isCustom = isCustomFrequency,
            customNumber = customFrequencyNumber,
            customUnit = customFrequencyUnit,
            customFrequencyError = customFrequencyError,
            onOptionSelected = vm::selectFrequencyOption,
            onCustomSelected = vm::selectCustomFrequency,
            onCustomNumberChange = vm::onCustomFrequencyNumberChange,
            onCustomUnitChange = vm::onCustomFrequencyUnitChange,
            onCancel = vm::closeFrequencyDialog,
            onDone = {
                val success = vm.confirmFrequency()
                if (success) { vm.closeFrequencyDialog()
                    if (frequencyDraft == "As needed") {
                        showAsNeededGuide = true
                    }
                }
            }
        )
    }

    if (showImageOptions) {
        AlertDialog(
            onDismissRequest = {
                showImageOptions = false
            },

            title = {
                Text(
                    text = "Select Image",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Text("Choose from Gallery")
                    }
                    TextButton(
                        onClick = {
                            showImageOptions = false
                            showPresetImages = true
                        }
                    ) {
                        Text("Choose Preset")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageOptions = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = EditGrey
                    )
                }
            }
        )
    }

    if (showPresetImages) {

        val presetImages = listOf(
            R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24,
            R.drawable.medication_liquid_24dp_1f1f1f_fill0_wght400_grad0_opsz24,
            R.drawable.fluid_24dp_1f1f1f_fill0_wght400_grad0_opsz24,
            R.drawable.surgical_24dp_1f1f1f_fill0_wght400_grad0_opsz24
        )
        AlertDialog(
            onDismissRequest = {
                showPresetImages = false
            },
            title = {
                Text(
                    text = "Choose Medicine Image",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(presetImages) { imageRes ->

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = Color(0xFFF5F6F7),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    vm.onPresetImageSelected(
                                        imageRes
                                    )

                                    showPresetImages = false
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = painterResource(
                                    imageRes
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        showPresetImages = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = EditGrey
                    )
                }
            }
        )
    }
    if (showAsNeededGuide) {
        AsNeededGuideDialog(
            onDismiss = {
                showAsNeededGuide = false
            }
        )
    }
}

@Composable
private fun AsNeededGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "How to take your medicine",
                style = MaterialTheme.typography.titleMedium
            )
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.medication_reminder_android
                    ),
                    contentDescription = "Take Now guide",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("For ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF009688),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("\"As needed\"")
                        }
                        append(
                            " medicine, you can record a dose anytime by tapping "
                        )
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF009688),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Take Now")
                        }
                        append(
                            " on the "
                        )
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF009688),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("\"Medicine Details screen\"")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },

        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditGreen
                )
            ) {
                Text("Got it")
            }
        }
    )
}
@Composable
private fun MedicineImageSelector(
    presetImageRes: Int?,
    galleryImageUri: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFF5F6F7),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = EditBorder,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        when {

            galleryImageUri != null -> {
                AsyncImage(
                    model = galleryImageUri,
                    contentDescription = "Medicine image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            presetImageRes != null -> {
                Image(
                    painter = painterResource(presetImageRes),
                    contentDescription = "Medicine image",
                    modifier = Modifier.size(56.dp)
                )
            }
            else -> {
                Icon(
                    painter = painterResource(
                        R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "Medicine image",
                    tint = EditGrey,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}

@Composable
private fun NumberInputField(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    allowDecimal: Boolean = false,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val valid = if (allowDecimal) {
                    newValue.isEmpty() || newValue.matches(Regex("""\d*\.?\d*"""))
                } else
                    newValue.all { it.isDigit() }
                if (valid) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = EditGrey
                    )
                }
            },
            singleLine = true,
            isError = isError,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (allowDecimal) {
                    KeyboardType.Decimal
                } else
                    KeyboardType.Number
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditGreen,
                unfocusedBorderColor = EditBorder,
                errorBorderColor = EditRed,
                errorCursorColor = EditRed,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                cursorColor = EditGreen
            )
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = EditRed
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
private fun FrequencyField(
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Frequency",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
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
                    color = EditBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select frequency",
                tint = EditGrey,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun FrequencyDialog(
    frequencyDraft: String,
    isCustom: Boolean,
    customNumber: String,
    customUnit: String,
    customFrequencyError: Boolean,
    onOptionSelected: (String) -> Unit,
    onCustomSelected: () -> Unit,
    onCustomNumberChange: (String) -> Unit,
    onCustomUnitChange: (String) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    val commonOptions = listOf(
        "Once a day",
        "Twice a day",
        "3 times a day",
        "Once a week",
        "As needed"
    )
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Select Frequency",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                commonOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                            }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isCustom && frequencyDraft == option,
                            onClick = { onOptionSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                // Custom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCustomSelected()
                        }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCustom,
                        onClick = onCustomSelected
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Custom frequency",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (isCustom) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Repeat every",
                        style = MaterialTheme.typography.bodySmall,
                        color = EditGrey
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberInputField(
                            value = customNumber,
                            onValueChange = onCustomNumberChange,
                            placeholder = "E.g. 2",
                            isError = customFrequencyError,
                            modifier = Modifier.width(80.dp)
                        )

                        DropdownLikeBox(
                            value = customUnit,
                            options = listOf(
                                "Hours",
                                "Days",
                                "Weeks",
                                "Months"
                            ),
                            onValueSelected = onCustomUnitChange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (customFrequencyError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please enter a value greater than 0.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EditRed
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDone
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = EditGreen
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = EditGrey
                )
            }
        }
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = EditGrey
                    )
                }
            },
            singleLine = singleLine,
            minLines = minLines,
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditGreen,
                unfocusedBorderColor = EditBorder,
                errorBorderColor = EditRed,
                errorCursorColor = EditRed,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                cursorColor = EditGreen
            )
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = EditRed
            )
        }
    }
}

@Composable
private fun DropdownLikeBox(
    value: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
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
                    color = EditBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    expanded = true
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.heightIn(
                max = 250.dp
            )
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReminderTimeRow(
    time: String,
    minutes: String,
    minutesError: String? = null,
    reminderOptionsEnabled: Boolean,
    onTimeChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onReminderOptionsClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker = true
                }
            , verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Tap to change time",
                    style = MaterialTheme.typography.bodySmall,
                    color = EditGrey
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Change time",
                tint = EditGrey,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .border(
                        width = 1.dp,
                        color = EditRed,
                        shape = CircleShape
                    )
                    .clickable {
                        onRemoveClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remove time",
                    tint = EditRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        //Reminder Options
        TextButton(
            onClick = onReminderOptionsClick,
            contentPadding = PaddingValues(
                horizontal = 0.dp,
                vertical = 0.dp
            )
        ) {
            Text(
                text = if (reminderOptionsEnabled) {
                    "- Hide reminder options"
                } else {
                    "+ Reminder options"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = EditGreen
            )
        }

        if (reminderOptionsEnabled) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Remind every",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditGrey
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    NumberInputField(
                        value = minutes,
                        onValueChange = onMinutesChange,
                        placeholder = "E.g. 10",
                        isError = minutesError != null,
                        modifier = Modifier.width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditGrey
                    )
                }
            }
        }

        if (minutesError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = minutesError,
                style = MaterialTheme.typography.bodySmall,
                color = EditRed
            )
        }
        // ==============================
        // Time Picker
        // ==============================

        if (showTimePicker) {
            val formatter =
                DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    Locale.ENGLISH
                )
            val parsedTime =
                try {
                    LocalTime.parse(
                        time.uppercase(Locale.ENGLISH),
                        formatter
                    )
                } catch (_: Exception) {
                    LocalTime.of(9, 0)
                }
            MedicationTimePickerDialog(
                initialTime = parsedTime,
                onDismiss = {
                    showTimePicker = false
                },
                onConfirm = { selectedTime ->
                    val formattedTime = selectedTime.format(formatter)
                    onTimeChange(formattedTime)
                    showTimePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = EditBorder,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    showDatePicker = true
                }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Select date",
                tint = EditGrey,
                modifier = Modifier.size(22.dp)
            )
        }
    }

    if (showDatePicker) {
        StartDatePickerDialog(
            currentDate = value,
            onDismiss = {
                showDatePicker = false
            },
            onConfirm = { selectedDate ->
                onValueChange(selectedDate)
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartDatePickerDialog(
    currentDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern(
        "dd MMM yyyy",
        Locale.ENGLISH
    )

    val parsedDate = try {
        LocalDate.parse(
            currentDate,
            formatter
        )
    } catch (_: Exception) { getMalaysiaDate() }
    val initialMillis = parsedDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selectedDate = Instant
                            .ofEpochMilli(selectedMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onConfirm(selectedDate.format(formatter))
                    }
                }
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = EditGreen
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = EditGrey
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}

@Composable
private fun RefillReminderSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.scale(0.9f),
        thumbContent = null,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = EditGreen,
            checkedBorderColor = EditGreen,

            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFBDBDBD),
            uncheckedBorderColor = Color(0xFFBDBDBD)
        )
    )
}

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