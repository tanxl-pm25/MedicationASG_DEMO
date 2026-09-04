package com.example.medication_demo.waterIntake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.ui.AppTopBar
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.viewmodel.WaterIntakeViewModel
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

private val WaterBlue = Color(0xFF168FF0)
private val WaterLightBlue = Color(0xFFAED2F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeScreen(
    onBack: () -> Unit = {},
    viewModel: WaterIntakeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditGoalDialog by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val today = getMalaysiaDate()
    val canEditGoal = uiState.selectedDate == today
    val isBeforeWaterStarted =
        viewModel.isSelectedDateBeforeWaterStarted()

    val selectedDateText =
        if (uiState.selectedDate == today) {
            "Today, " + uiState.selectedDate.format(
                DateTimeFormatter.ofPattern(
                    "dd MMM",
                    Locale.ENGLISH
                )
            )
        } else {
            uiState.selectedDate.format(
                DateTimeFormatter.ofPattern(
                    "dd MMM",
                    Locale.ENGLISH
                )
            )
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Water Intake",
                onBackClick = onBack,
                onCalendarClick = {
                    showDatePicker = true
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDateText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(14.dp))
            }

            if (isBeforeWaterStarted) {
                Spacer(modifier = Modifier.height(70.dp))

                WaterBeforeStartState()

                Spacer(modifier = Modifier.weight(1f))
            } else if (uiState.dailyGoal == 0) {
                Spacer(modifier = Modifier.height(70.dp))

                EmptyWaterGoalState(
                    onSetGoalClick = {
                        showEditGoalDialog = true
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WaterProgressCircle(
                        progress = uiState.progress,
                        glasses = uiState.glasses,
                        goal = uiState.dailyGoal
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-8).dp)
                            .size(50.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.removeGlass()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Remove glass",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(35.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 8.dp)
                            .size(50.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.addGlass()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add glass",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Text(
                    text = uiState.message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(uiState.dailyGoal) { index ->
                        WaterGlass(
                            filled = index < uiState.glasses
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 90.dp)
                        .height(130.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 24.dp,
                            vertical = 22.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Daily Goal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text =
                                if (uiState.dailyGoal == 0) {
                                    "No goal set"
                                } else {
                                    "${uiState.dailyGoal} Glasses"
                                },
                            fontSize = 18.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(58.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                enabled = canEditGoal
                            ) {
                                showEditGoalDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =
                                if (!canEditGoal) {
                                    "Today only"
                                } else if (uiState.dailyGoal == 0) {
                                    "Set Goal"
                                } else {
                                    "Edit Goal"
                                },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color =
                                if (canEditGoal) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                }
                        )
                    }
                }
            }
        }

        if (showEditGoalDialog) {
            EditWaterGoalDialog(
                currentGoal = uiState.dailyGoal,
                onDismiss = {
                    showEditGoalDialog = false
                },
                onConfirm = { newGoal ->
                    viewModel.updateGoal(newGoal)
                    showEditGoalDialog = false
                }
            )
        }
    }

    if (showDatePicker) {
        val selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(
                    utcTimeMillis: Long
                ): Boolean {
                    val date = Instant
                        .ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    return !date.isAfter(
                        getMalaysiaDate()
                    )
                }

                override fun isSelectableYear(
                    year: Int
                ): Boolean {
                    return year <= getMalaysiaDate().year
                }
            }
        }

        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis =
                uiState.selectedDate
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli(),
            selectableDates = selectableDates
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            viewModel.selectDate(
                                year = selectedDate.year,
                                month = selectedDate.monthValue,
                                day = selectedDate.dayOfMonth
                            )

                            showDatePicker = false
                        }
                    }
                ) {
                    Text(
                        text = "Done",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun EditWaterGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goal by remember(currentGoal) {
        mutableIntStateOf(
            if (currentGoal > 0) {
                currentGoal
            } else {
                1
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Text(
                text = "Edit Daily Goal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set your daily water intake goal.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color =
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .clickable {
                                if (goal > 1) {
                                    goal--
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Decrease goal",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = goal.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Glasses",
                            fontSize = 14.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color =
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .clickable {
                                if (goal < 20) {
                                    goal++
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Increase goal",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
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
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(goal)
                }
            ) {
                Text(
                    text = "Save"
                )
            }
        }
    )
}

@Composable
fun WaterProgressCircle(
    progress: Float,
    glasses: Int,
    goal: Int
) {
    val backgroundCircleColor =
        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 18.dp.toPx()

            val diameter =
                min(size.width, size.height) - strokeWidth

            val topLeft = Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )

            val arcSize = Size(
                diameter,
                diameter
            )

            drawArc(
                color = backgroundCircleColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = WaterBlue,
                startAngle = -90f,
                sweepAngle =
                    360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = glasses.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = " / $goal",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 7.dp)
                )
            }

            Text(
                text = "Glasses",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WaterBeforeStartState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = WaterBlue.copy(alpha = 0.14f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = WaterBlue,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No water record",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text =
                "This date is before you started " +
                        "tracking water intake.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyWaterGoalState(
    onSetGoalClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = WaterBlue.copy(alpha = 0.14f),
                    shape = CircleShape
                )
                .clickable {
                    onSetGoalClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Set water goal",
                tint = WaterBlue,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Set your daily water goal",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tap + to set your daily water goal",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WaterGlass(
    filled: Boolean
) {
    Canvas(
        modifier = Modifier
            .width(38.dp)
            .height(58.dp)
    ) {
        val outlineColor =
            if (filled) {
                WaterBlue
            } else {
                WaterLightBlue
            }

        val glassPath = Path()

        glassPath.moveTo(
            size.width * 0.15f,
            size.height * 0.08f
        )

        glassPath.lineTo(
            size.width * 0.85f,
            size.height * 0.08f
        )

        glassPath.lineTo(
            size.width * 0.76f,
            size.height * 0.92f
        )

        glassPath.lineTo(
            size.width * 0.24f,
            size.height * 0.92f
        )

        glassPath.close()

        drawPath(
            path = glassPath,
            color = outlineColor,
            style = Stroke(
                width = 4.dp.toPx()
            )
        )

        if (filled) {
            drawRect(
                color = WaterBlue,
                topLeft = Offset(
                    size.width * 0.20f,
                    size.height * 0.48f
                ),
                size = Size(
                    size.width * 0.60f,
                    size.height * 0.42f
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun WaterIntakeScreenPreview() {
    Medication_DemoTheme {
        WaterIntakeScreen()
    }
}