package com.example.medication_demo.statistics

import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.model.MissedMedicine
import com.example.medication_demo.viewmodel.MissedMedicationViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val Green = Color(0xFF188A45)
val LightGreen = Color(0xFFEAF7EF)
val Red = Color(0xFFE53935)
val LightRed = Color(0xFFFFF1F1)
val DarkText = Color(0xFF202124)
val GreyText = Color(0xFF73777D)
val LightGrey = Color(0xFFF5F5F5)
val BorderGrey = Color(0xFFE9E9E9)

// SCREEN
@Composable
fun MissedMedicationScreen(
    onBackClick: () -> Unit = {},
    viewModel: MissedMedicationViewModel = viewModel()
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
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkText
                )
            }

            Text(
                text = "Missed Medication",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.size(25.dp))
        }

        // MONTH SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF8F9F8))
                    .clickable {
                        viewModel.previousMonth()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFF8F9F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedMonth,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF8F9F8))
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
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // SUMMARY CARD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 8.dp,
                    bottom = 10.dp
                )
                .clip(RoundedCornerShape(10.dp))
                .background(LightRed)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Red circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Red),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = uiState.missedCount.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Red
                )

                Text(
                    text = "Missed Doses",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "You missed ${uiState.missedCount} scheduled medication\n" +
                            "doses this month.",
                    fontSize = 8.sp,
                    color = GreyText,
                    lineHeight = 12.sp
                )
            }

            // Calendar illustration
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFDCDC)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFFFF8A8A),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // MISSED MEDICATION LIST
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 22.dp,
                end = 22.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(uiState.missedMedicines) { medicine ->

                MissedMedicationCard(
                    medicine = medicine
                )
            }
        }
    }
}


// MISSED MEDICATION CARD
@Composable
fun MissedMedicationCard(
    medicine: MissedMedicine

) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // DATE
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = medicine.day,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = medicine.month,
                fontSize = 8.sp,
                color = DarkText
            )

            Text(
                text = "(${medicine.weekday})",
                fontSize = 8.sp,
                color = GreyText
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(48.dp)
                .background(BorderGrey)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // RED DOT
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Red)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // MEDICINE INFORMATION
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = medicine.medicineName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GreyText,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Scheduled: ${medicine.scheduledTime}",
                    fontSize = 8.sp,
                    color = GreyText
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Dosage: ${medicine.dosage}",
                fontSize = 8.sp,
                color = GreyText
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Missed",
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = Red
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun MissedMedicationScreenPreview() {
    MaterialTheme {
        MissedMedicationScreen()
    }
}