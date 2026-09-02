package com.example.medication_demo.statistics

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.MedicationPerformanceViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview



// MEDICATION PERFORMANCE SCREEN
@Composable
fun MedicationPerformanceScreen(
    onBack: () -> Unit = {},
    viewModel: MedicationPerformanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val selectedMonth =
        uiState.selectedMonth.format(
            DateTimeFormatter.ofPattern(
                "MMMM yyyy",
                Locale.ENGLISH
            )
        )

    val canGoNext =
        uiState.selectedMonth.isBefore(
            java.time.YearMonth.now()
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp)
    ) {


        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF26364D),
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onBack()
                    }
            )

            Text(
                text = "Medication Performance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF18263C),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

        }


        // MONTH SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Previous month
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = Color(0xFFF8F9FA),
                        shape = CircleShape
                    )
                    .clickable {
                        viewModel.previousMonth()
                    },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = Color(0xFF89919A),
                    modifier = Modifier.size(24.dp)
                )
            }


            Spacer(modifier = Modifier.width(10.dp))


            // Current month
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .background(
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = selectedMonth,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3D4650)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            // Next month
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color(0xFFF8F9FA),
                        shape = CircleShape
                    )
                    .alpha(
                        if (canGoNext) 1f else 0.35f
                    )
                    .clickable(
                        enabled = canGoNext
                    ) {
                        viewModel.nextMonth()
                    },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Next month",
                    tint = Color(0xFF89919A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))


        if (uiState.medications.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            EmptyMedicationPerformanceState()
            Spacer(modifier = Modifier.weight(1f))

        } else {

            uiState.medications.forEachIndexed { index, medication ->

                MedicationPerformanceCard(
                    medicationName = medication.medicationName,
                    taken = medication.taken,
                    total = medication.total,
                    missed = medication.missed,
                    iconColor = medication.iconColor
                )

                if (index < uiState.medications.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            KeepItUpCard()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MedicationPerformanceCard(
    medicationName: String,
    taken: Int,
    total: Int,
    missed: Int,
    iconColor: Color
) {

    val progress = if (total > 0) taken.toFloat() / total.toFloat()
    else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                )
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Medicine Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = Color(0xFFF5F6F7),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Medication,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Medication Name
                Text(
                    text = medicationName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )


                // Taken / Total
                Text(
                    text = "$taken/$total",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF159447)
                )
            }


            Spacer(modifier = Modifier.height(5.dp))


            // PROGRESS BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Color(0xFFE6E8EA)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            Color(0xFF159447)
                        )
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // TAKEN / MISSED
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Taken",
                    fontSize = 13.sp,
                    color = Color(0xFF159447)
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Missed $missed",
                    fontSize = 13.sp,
                    color = Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun EmptyMedicationPerformanceState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(0xFFEAF8F1),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                tint = Color(0xFF159447),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No medication performance yet",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF26364D)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your medication progress will appear here.",
            fontSize = 14.sp,
            color = Color(0xFF89919A),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun KeepItUpCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color = Color(0xFFF5FBF7),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ICON
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color(0xFFE6F6EB),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF159447),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // TEXT
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Keep it up!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "You're doing great staying on track.",
                fontSize = 12.sp,
                color = Color(0xFF777777)
            )
        }

        // ARROW
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF858585),
            modifier = Modifier.size(17.dp)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun MedicationPerformanceScreenPreview() {

    MaterialTheme {
        MedicationPerformanceScreen()
    }
}