package com.example.medication_demo.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.model.DoseStatus
import com.example.medication_demo.model.MedicineDailyHistoryUi
import com.example.medication_demo.model.MedicineDoseUi
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MedicineHistoryDetailScreen(
    medicineName: String,
    dailyHistory: List<MedicineDailyHistoryUi>,
    onBackClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern(
        "dd MMM yyyy",
        Locale.ENGLISH
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 28.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "$medicineName History",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
        ) {
            if (dailyHistory.isEmpty()) {
                Spacer(
                    modifier = Modifier.height(30.dp)
                )

                Text(
                    text = "No history record found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                dailyHistory.forEach { day ->
                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(22.dp)
                                        .background(
                                            color =
                                                MaterialTheme.colorScheme.primary,
                                            shape =
                                                RoundedCornerShape(50)
                                        )
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = day.date.format(
                                        dateFormatter
                                    ),
                                    style =
                                        MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color =
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color =
                                        MaterialTheme.colorScheme.primary
                                            .copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text =
                                            "Taken ${day.takenCount}",
                                        color =
                                            MaterialTheme.colorScheme.primary,
                                        style =
                                            MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color =
                                        MaterialTheme.colorScheme.error
                                            .copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text =
                                            "Missing ${day.missingCount}",
                                        color =
                                            MaterialTheme.colorScheme.error,
                                        style =
                                            MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            day.doses.forEachIndexed { index, dose ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = dose.time,
                                        style =
                                            MaterialTheme.typography.bodyLarge,
                                        color =
                                            MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dose.dosage,
                                            style =
                                                MaterialTheme.typography.bodySmall,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                        )

                                        Spacer(
                                            modifier = Modifier.width(6.dp)
                                        )

                                        Text(
                                            text = "•",
                                            style =
                                                MaterialTheme.typography.bodySmall,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                        )

                                        Spacer(
                                            modifier = Modifier.width(6.dp)
                                        )

                                        Text(
                                            text = day.frequency,
                                            style =
                                                MaterialTheme.typography.bodySmall,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }

                                    when (dose.status) {
                                        DoseStatus.TAKEN -> {
                                            if (dose.takenTime != null) {
                                                Spacer(
                                                    modifier =
                                                        Modifier.height(5.dp)
                                                )

                                                Text(
                                                    text =
                                                        "Taken at ${dose.takenTime}",
                                                    style =
                                                        MaterialTheme.typography
                                                            .bodySmall,
                                                    color =
                                                        MaterialTheme.colorScheme
                                                            .primary
                                                )
                                            }
                                        }

                                        DoseStatus.MISSING -> {
                                            Spacer(
                                                modifier =
                                                    Modifier.height(5.dp)
                                            )

                                            Text(
                                                text =
                                                    "Missing at ${dose.time}",
                                                style =
                                                    MaterialTheme.typography
                                                        .bodySmall,
                                                color =
                                                    MaterialTheme.colorScheme
                                                        .error
                                            )
                                        }

                                        else -> Unit
                                    }
                                }

                                if (index < day.doses.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            vertical = 6.dp
                                        ),
                                        color =
                                            MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MedicineHistoryDetailScreenPreview() {
    val previewHistory = listOf(
        MedicineDailyHistoryUi(
            date = LocalDate.of(
                2026,
                8,
                29
            ),
            frequency = "Once a day",
            takenCount = 0,
            missingCount = 1,
            doses = listOf(
                MedicineDoseUi(
                    time = "12:00 AM",
                    medicineName = "Panadol",
                    dosage = "1 Tablet",
                    status = DoseStatus.MISSING,
                    extraText = null,
                    takenTime = null
                )
            )
        ),

        MedicineDailyHistoryUi(
            date = LocalDate.of(
                2026,
                8,
                30
            ),
            frequency = "Once a day",
            takenCount = 1,
            missingCount = 0,
            doses = listOf(
                MedicineDoseUi(
                    time = "12:00 AM",
                    medicineName = "Panadol",
                    dosage = "1 Tablet",
                    status = DoseStatus.TAKEN,
                    extraText = null,
                    takenTime = "08:15 AM"
                )
            )
        ),

        MedicineDailyHistoryUi(
            date = LocalDate.of(
                2026,
                9,
                1
            ),
            frequency = "Twice a day",
            takenCount = 1,
            missingCount = 1,
            doses = listOf(
                MedicineDoseUi(
                    time = "08:00 AM",
                    medicineName = "Panadol",
                    dosage = "2 Tablets",
                    status = DoseStatus.TAKEN,
                    extraText = null,
                    takenTime = "08:05 AM"
                ),

                MedicineDoseUi(
                    time = "08:00 PM",
                    medicineName = "Panadol",
                    dosage = "2 Tablets",
                    status = DoseStatus.MISSING,
                    extraText = null,
                    takenTime = null
                )
            )
        )
    )

    Medication_DemoTheme {
        MedicineHistoryDetailScreen(
            medicineName = "Panadol",
            dailyHistory = previewHistory,
            onBackClick = {}
        )
    }
}