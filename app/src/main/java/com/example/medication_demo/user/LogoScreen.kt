package com.example.medication_demo.user

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val LeafGreenLight = Color(0xFFDCEEDD)
private val LeafGreenMid = Color(0xFFB6D9BC)
private val WaveGreen = Color(0xFFA9D3AE)
private val TitleDark = Color(0xFF13342A)
private val SubtitleGray = Color(0xFF6B7A75)

@Composable
fun LogoScreen(
    onNextClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNextClick()
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // top-left blob
                drawCircle(
                    color = LeafGreenLight,
                    radius = w * 0.55f,
                    center = Offset(x = -w * 0.15f, y = h * 0.05f)
                )

                // top-right small circle
                drawCircle(
                    color = LeafGreenLight,
                    radius = w * 0.18f,
                    center = Offset(x = w * 0.95f, y = h * 0.12f)
                )

                // mid-left small circle
                drawCircle(
                    color = LeafGreenLight,
                    radius = w * 0.09f,
                    center = Offset(x = w * 0.1f, y = h * 0.28f)
                )

                // bottom waves
                val wavePath1 = Path().apply {
                    moveTo(0f, h * 0.82f)
                    cubicTo(
                        w * 0.25f, h * 0.75f,
                        w * 0.75f, h * 0.95f,
                        w, h * 0.85f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(path = wavePath1, color = WaveGreen.copy(alpha = 0.4f))

                val wavePath2 = Path().apply {
                    moveTo(0f, h * 0.9f)
                    cubicTo(
                        w * 0.3f, h * 0.82f,
                        w * 0.7f, h,
                        w, h * 0.9f
                    )
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(path = wavePath2, color = WaveGreen.copy(alpha = 0.7f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoo_icon),
                    contentDescription = "App icon",
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = "Smart Medication\nCompanion",
                    fontSize = 30.sp,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your health, our priority.",
                    fontSize = 16.sp,
                    color = SubtitleGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LogoScreenPreview() {
    Medication_DemoTheme {
        LogoScreen()
    }
}