package com.example.medication_demo.statistics

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import  com.example.medication_demo.viewmodel.MonthlyStatisticsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.ui.AppTopBar
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MonthlyStatisticsScreen(
    onBack: () -> Unit = {},
    onMedicationPerformanceClick: () -> Unit = {},
    onMissedMedicationClick: () -> Unit = {},
    viewModel: MonthlyStatisticsViewModel = viewModel()
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

    Scaffold(
        containerColor = Color.White,
        topBar = {
            AppTopBar(
                title = "Monthly Statistics",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(14.dp))

            // MONTH SELECTOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color(0xFFF8F9F8),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            viewModel.previousMonth()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous",
                        tint = Color(0xFF777777),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .background(
                            color = Color(0xFFFAFAFA),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = selectedMonth,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color(0xFFF8F9F8),
                            shape = RoundedCornerShape(50)
                        )
                        .alpha(if (canGoNext) 1f else 0.35f)
                        .clickable(enabled = canGoNext) {
                            viewModel.nextMonth()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next",
                        tint = Color(0xFF777777),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }




            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.totalDoses == 0) {

                Spacer(modifier = Modifier.weight(1f))

                EmptyMonthlyStatisticsState(
                    selectedMonth = selectedMonth
                )

                Spacer(modifier = Modifier.weight(1f))

            } else {
                // MEDICATION ADHERENCE CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5FBF7)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(
                            start = 14.dp,
                            top = 10.dp
                        )
                    ) {

                        Text(
                            text = "Medication Adherence",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF555555)
                        )

                        Text(
                            text = "${uiState.adherencePercentage}%",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF20A447)
                        )

                        Text(
                            text = uiState.adherenceMessage,
                            fontSize = 12.sp,
                            color = Color(0xFF57A96C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ADHERENCE OVER TIME CHART (bar chart)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 10.dp,
                            top = 10.dp
                        )
                    ) {

                        Text(
                            text = "Adherence Over Time",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF444444)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        AdherenceChart(
                            values = uiState.chartValues,
                            labels = uiState.chartLabels
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // STATISTICS CARDS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFF20A447),
                        title = "Taken",
                        value = uiState.takenDoses.toString(),
                        unit = "doses"
                    )

                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Cancel,
                        iconColor = Color(0xFFE53935),
                        title = "Missed",
                        value = uiState.missedDoses.toString(),
                        unit = "doses"
                    )

                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Description,
                        iconColor = Color(0xFF4285D4),
                        title = "Total",
                        value = uiState.totalDoses.toString(),
                        unit = "doses"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MEDICATION PERFORMANCE
                StatisticsMenuItem(
                    icon = Icons.Default.Medication,
                    iconColor = Color(0xFF36A957),
                    backgroundColor = Color(0xFFEFFAF2),
                    title = "Medication Performance",
                    subtitle = "See how each medication performed this month.",
                    onClick = onMedicationPerformanceClick
                )

                Spacer(modifier = Modifier.height(8.dp))


                // MISSED MEDICATION
                StatisticsMenuItem(
                    icon = Icons.Default.EventBusy,
                    iconColor = Color(0xFFFF4A4A),
                    backgroundColor = Color(0xFFFFF2F2),
                    title = "Missed Medication",
                    subtitle = "View the list of missed medications this month.",
                    onClick = onMissedMedicationClick
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}




@Composable
fun AdherenceChart(
    values: List<Int>,
    labels: List<String>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
    ) {

        val chartLeft = 28f
        val chartRight = size.width - 8f
        val chartTop = 5f
        val chartBottom = size.height - 18f

        val chartHeight = chartBottom - chartTop

        // Horizontal grid lines
        val gridValues = listOf(
            100,
            75,
            50,
            25,
            0
        )

        gridValues.forEach { value ->

            val y = chartBottom -
                    (value / 100f) * chartHeight

            drawLine(
                color = Color(0xFFE8E8E8),
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(4f, 4f)
                )
            )
        }

        // Y-axis labels
        // 100%
        drawContext.canvas.nativeCanvas.drawText(
            "100%",
            0f,
            chartTop + 5f,
            android.graphics.Paint().apply {
                textSize = 22f
                color = android.graphics.Color.GRAY
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "75%",
            5f,
            chartTop + chartHeight * 0.25f + 3f,
            android.graphics.Paint().apply {
                textSize = 22f
                color = android.graphics.Color.GRAY
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "50%",
            5f,
            chartTop + chartHeight * 0.5f + 3f,
            android.graphics.Paint().apply {
                textSize = 22f
                color = android.graphics.Color.GRAY
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "25%",
            5f,
            chartTop + chartHeight * 0.75f + 3f,
            android.graphics.Paint().apply {
                textSize = 22f
                color = android.graphics.Color.GRAY
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "0%",
            10f,
            chartBottom + 3f,
            android.graphics.Paint().apply {
                textSize = 22f
                color = android.graphics.Color.GRAY
            }
        )


        // Bars
        val barWidth = 30f

        val spacing =
            (chartRight - chartLeft) / values.size

        values.forEachIndexed { index, value ->

            val barHeight =
                (value / 100f) * chartHeight

            val x =
                chartLeft +
                        spacing * index +
                        spacing / 2f -
                        barWidth / 2f

            val top =
                chartBottom - barHeight

            drawRoundRect(
                color = Color(0xFF209B43),
                topLeft = Offset(x, top),
                size = androidx.compose.ui.geometry.Size(
                    barWidth,
                    barHeight
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    2f,
                    2f
                )
            )
        }


        // X-axis labels
        val paint = android.graphics.Paint().apply {
            textSize = 20f
            color = android.graphics.Color.GRAY
            textAlign = android.graphics.Paint.Align.CENTER
        }

        labels.forEachIndexed { index, label ->

            val x =
                chartLeft +
                        spacing * index +
                        spacing / 2f

            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                size.height - 0f,
                paint
            )
        }
    }
}

@Composable
fun StatisticCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    unit: String
) {

    Card(
        modifier = modifier
            .height(85.dp),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = title,
                    fontSize = 8.sp,
                    color = Color(0xFF555555)
                )
            }

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )

            Text(
                text = unit,
                fontSize = 7.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
fun StatisticsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        // Icon box
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Text
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = Color(0xFF888888)
            )
        }


        // Arrow
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF777777),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun EmptyMonthlyStatisticsState(
    selectedMonth: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = Color(0xFFEAF8F1),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = null,
                tint = Color(0xFF159447),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No medication data yet",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF26364D)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your statistics for $selectedMonth will appear here.",
            fontSize = 14.sp,
            color = Color(0xFF89919A),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun MonthlyStatisticsScreenPreview() {

    MaterialTheme {
        MonthlyStatisticsScreen()
    }
}