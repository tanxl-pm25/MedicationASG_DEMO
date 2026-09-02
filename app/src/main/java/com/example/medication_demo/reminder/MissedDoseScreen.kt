package com.example.medication_demo.reminder

import androidx.compose.material3.TextButton
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.medication_demo.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.viewmodel.MedicationMissedViewModel
import com.example.medication_demo.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MissedDoseScreen(
    onBackClick: () -> Unit = {},
    onRescheduleClick: () -> Unit = {},
    onSkipClick: () -> Unit = {},
    viewModel: MedicationMissedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        // TOP BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            // Back arrow
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = DarkText,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            // More icon
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More",
                tint = DarkText,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(25.dp)
            )
        }


        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // MEDICATION NAME
            Text(
                text = uiState.medicineName,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uiState.dosage} • ${uiState.scheduledTime}",
                fontSize = 17.sp,
                color = TextGrey
            )

            Spacer(modifier = Modifier.height(24.dp))

            // MISSED MEDICATION CIRCLE
            Box(
                modifier = Modifier.size(270.dp),
                contentAlignment = Alignment.Center
            ) {
                // RED ALARM CLOCK
                Image(
                    painter = painterResource(
                        id = R.drawable.missed_medication_clock
                    ),
                    contentDescription = "Missed medication",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // MISSED MESSAGE
            Text(
                text = "Missed at ${uiState.scheduledTime}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = uiState.message,
                fontSize = 17.sp,
                color = TextGrey
            )

        }

        // BOTTOM BUTTONS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // RESCHEDULE BUTTON
            Button(
                onClick = onRescheduleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppRed,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Reschedule",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }


            // SKIP BUTTON
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = "Skip",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 390,
    heightDp = 844
)

@Composable
private fun MedicationMissedScreenPreview() {
    MaterialTheme {
        MissedDoseScreen()
    }
}