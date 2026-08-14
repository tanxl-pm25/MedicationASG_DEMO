package com.example.medication_demo.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val ReminderGreen = Color(0xFF159447)
private val ReminderBackground = Color.White
private val ReminderTextGrey = Color(0xFF555555)

@Composable
fun RefillReminderScreen(
    medicineName: String = "Metformin",
    tabletsLeft: Int = 5,
    onRemindAgainClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    var message by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        containerColor = ReminderBackground
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "Low medicine quantity reminder",
                    modifier = Modifier.size(190.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Running Low!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "You have $tabletsLeft tablets left of\n$medicineName.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReminderTextGrey,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "It’s time to refill your\n$medicineName.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReminderTextGrey,
                    textAlign = TextAlign.Center
                )

                if (message != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = message!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = ReminderGreen,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        message = "We will remind you again later."
                        onRemindAgainClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReminderGreen
                    )
                ) {
                    Text(
                        text = "Remind Again",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDoneClick
                ) {
                    Text(
                        text = "Done",
                        style = MaterialTheme.typography.labelLarge,
                        color = ReminderGreen
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RefillReminderScreenPreview() {
    Medication_DemoTheme {
        RefillReminderScreen()
    }
}
