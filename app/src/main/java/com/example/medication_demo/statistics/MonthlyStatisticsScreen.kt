package com.example.medication_demo.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medication_demo.ui.AppTopBar
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.viewmodel.MonthlyStatisticsViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MonthlyStatisticsScreen(
    onBack: () -> Unit = {},
    onMissedMedicationClick: () -> Unit = {},
    viewModel: MonthlyStatisticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val selectedMonth = uiState.selectedMonth.format(
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale.ENGLISH
        )
    )

    val canGoNext = uiState.selectedMonth.isBefore(
        YearMonth.now()
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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

            MonthSelector(
                selectedMonth = selectedMonth,
                canGoNext = canGoNext,
                onPreviousMonth = {
                    viewModel.previousMonth()
                },
                onNextMonth = {
                    viewModel.nextMonth()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.totalDoses == 0) {
                Spacer(modifier = Modifier.weight(1f))

                EmptyMonthlyStatisticsState(
                    selectedMonth = selectedMonth
                )

                Spacer(modifier = Modifier.weight(1f))
            } else {
                MedicationAdherenceCard(
                    percentage = uiState.adherencePercentage,
                    message = uiState.adherenceMessage
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
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
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        AdherenceChart(
                            values = uiState.chartValues,
                            labels = uiState.chartLabels
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        iconColor = MaterialTheme.colorScheme.primary,
                        title = "Taken",
                        value = uiState.takenDoses.toString(),
                        unit = "doses"
                    )

                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Cancel,
                        iconColor = MaterialTheme.colorScheme.error,
                        title = "Missed",
                        value = uiState.missedDoses.toString(),
                        unit = "doses"
                    )

                    StatisticCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Description,
                        iconColor = Color(0xFF5B9FE3),
                        title = "Total",
                        value = uiState.totalDoses.toString(),
                        unit = "doses"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                StatisticsMenuItem(
                    icon = Icons.Default.EventBusy,
                    iconColor = MaterialTheme.colorScheme.error,
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
private fun MonthSelector(
    selectedMonth: String,
    canGoNext: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                )
                .clickable {
                    onPreviousMonth()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(50)
                )
                .alpha(
                    if (canGoNext) {
                        1f
                    } else {
                        0.35f
                    }
                )
                .clickable(enabled = canGoNext) {
                    onNextMonth()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun MedicationAdherenceCard(
    percentage: Int,
    message: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.12f
            )
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
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "$percentage%",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AdherenceChart(
    values: List<Int>,
    labels: List<String>
) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(
        alpha = 0.55f
    )
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val barColor = MaterialTheme.colorScheme.primary

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
                color = gridColor,
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(4f, 4f)
                )
            )
        }

        val axisPaint = android.graphics.Paint().apply {
            textSize = 22f
            color = labelColor.toArgb()
        }

        drawContext.canvas.nativeCanvas.drawText(
            "100%",
            0f,
            chartTop + 5f,
            axisPaint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "75%",
            5f,
            chartTop + chartHeight * 0.25f + 3f,
            axisPaint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "50%",
            5f,
            chartTop + chartHeight * 0.5f + 3f,
            axisPaint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "25%",
            5f,
            chartTop + chartHeight * 0.75f + 3f,
            axisPaint
        )

        drawContext.canvas.nativeCanvas.drawText(
            "0%",
            10f,
            chartBottom + 3f,
            axisPaint
        )

        if (values.isNotEmpty()) {
            val barWidth = 30f
            val spacing =
                (chartRight - chartLeft) / values.size

            values.forEachIndexed { index, value ->
                val safeValue = value.coerceIn(0, 100)
                val barHeight =
                    (safeValue / 100f) * chartHeight

                val x =
                    chartLeft +
                            spacing * index +
                            spacing / 2f -
                            barWidth / 2f

                val top = chartBottom - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, top),
                    size = Size(
                        barWidth,
                        barHeight
                    ),
                    cornerRadius = CornerRadius(
                        2f,
                        2f
                    )
                )
            }

            val xAxisPaint = android.graphics.Paint().apply {
                textSize = 20f
                color = labelColor.toArgb()
                textAlign = android.graphics.Paint.Align.CENTER
            }

            labels.forEachIndexed { index, label ->
                if (index < values.size) {
                    val x =
                        chartLeft +
                                spacing * index +
                                spacing / 2f

                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        x,
                        size.height,
                        xAxisPaint
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    modifier: Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    unit: String
) {
    Card(
        modifier = modifier.height(85.dp),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    color = MaterialTheme.colorScheme.onSurface
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatisticsMenuItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = iconColor.copy(alpha = 0.12f),
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.14f
                    ),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No medication data yet",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your statistics for $selectedMonth will appear here.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MonthlyStatisticsScreenPreview() {
    Medication_DemoTheme {
        MonthlyStatisticsScreen()
    }
}