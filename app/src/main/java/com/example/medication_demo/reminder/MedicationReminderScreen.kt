package com.example.medication_demo.reminder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.R
import com.example.medication_demo.ui.AppTopBar

private val ReminderGreen = Color(0xFF08A86A)
private val ReminderDarkText = Color(0xFF17233F)
private val ReminderTextGrey = Color(0xFF69738A)
private val ReminderBorder = Color(0xFF9DD8B2)

@Composable
fun MedicationReminderScreen(
    medicineName: String,
    dosage: String,
    scheduledTime: String,
    onBackClick: () -> Unit = {},
    onTakenClick: () -> Unit = {},
    onRescheduleClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = "Medication Reminder",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = medicineName,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = ReminderDarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$dosage • $scheduledTime",
                fontSize = 15.sp,
                color = ReminderTextGrey
            )

            Spacer(modifier = Modifier.height(42.dp))

            Image(
                painter = painterResource(
                    id = R.drawable.medication_reminder_pill
                ),
                contentDescription = "Medication reminder",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Time to take your medication",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ReminderDarkText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Mark it as taken once you have taken this dose.",
                fontSize = 15.sp,
                color = ReminderTextGrey
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 28.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onTakenClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReminderGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Taken",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onRescheduleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = ReminderBorder
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ReminderGreen
                )
            ) {
                Text(
                    text = "Reschedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicationReminderScreenPreview() {
    MaterialTheme {
        MedicationReminderScreen(
            medicineName = "Metformin",
            dosage = "1 Tablet",
            scheduledTime = "08:30 AM"
        )
    }
}