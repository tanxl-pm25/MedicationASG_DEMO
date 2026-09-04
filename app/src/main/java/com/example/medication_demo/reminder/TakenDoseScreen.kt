package com.example.medication_demo.reminder

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.medication_demo.R
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val AppGreen = Color(0xFF08A86A)
private val DarkText = Color(0xFF17233F)
private val TextGrey = Color(0xFF69738A)
private val LightGreen = Color(0xFFE6F7F0)
private val SoftGreen = Color(0xFFC8F0E2)

@Composable
fun TakenDoseScreen(
    medicineName: String,
    dosage: String,
    scheduledTime: String,
    takenTime: String,
    encouragementMessage: String =
        "Great job! Keep following your schedule.",
    onDoneClick: () -> Unit = {},
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            // MEDICATION NAME
            Text(
                text = medicineName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "$dosage • $scheduledTime",                fontSize = 14.sp,
                color = TextGrey
            )

            Spacer(modifier = Modifier.height(55.dp))

            // MEDICATION TAKEN ILLUSTRATION
            Image(
                painter = painterResource(
                    id = R.drawable.medication_taken_pill
                ),
                contentDescription = "Medication taken successfully",
                modifier = Modifier.size(270.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(50.dp))

            // TAKEN MESSAGE
            Text(
                text = "Taken at $takenTime 🎉",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                encouragementMessage,
                fontSize = 19.sp,
                color = TextGrey
            )
        }


        // DONE BUTTON
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 30.dp
                )
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppGreen
            )
        ) {

            Text(
                text = "Done",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}


// PREVIEW
@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TakenDoseScreenPreview() {

    MaterialTheme {
        TakenDoseScreen(
            medicineName = "Metformin",
            dosage = "1 Tablet",
            scheduledTime = "08:30 AM",
            takenTime = "08:32 AM"
        )
    }
}