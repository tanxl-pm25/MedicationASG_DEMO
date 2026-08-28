package com.example.medication_demo.user

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.ui.theme.Medication_DemoTheme

private val HomeGreen = Color(0xFF159447)
private  fun getCurrentDate(): String{
    val formatter = java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale.ENGLISH)
    return formatter.format(java.util.Date())
}

@Composable
fun HomeScreen(
    username: String,
    date: String = getCurrentDate(),
    nextMedicineName: String? = null,
    nextMedicineDose: String ?= null,
    nextMedicineTime: String? = null,
    hasUnreadNotofication: Boolean = false,
    onMarkAsTakenClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        selectedIconColor = HomeGreen,
                        selectedTextColor = HomeGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(
                        imageVector = Icons.Filled.Medication,
                        contentDescription = "Medicine"
                    ) },
                    label = { Text("Medicine") },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        selectedIconColor = HomeGreen,
                        selectedTextColor = HomeGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "History"
                    ) },
                    label = { Text("History") },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        selectedIconColor = HomeGreen,
                        selectedTextColor = HomeGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Filled.Person,
                        contentDescription = "Profile"
                    ) },
                    label = { Text("Profile") },
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        selectedIconColor = HomeGreen,
                        selectedTextColor = HomeGreen
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            // Top: greeting + bell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append("Good Morning,\n")
                            withStyle(
                                style = SpanStyle(
                                    color = HomeGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(username)
                            }
                            append(" \uD83D\uDC4B")
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box{
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsNone,
                            contentDescription = "Notifications"
                        )
                    }
                    if (hasUnreadNotofication){
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = Color.Red,
                                    shape = CircleShape
                                )
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Medicine
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = HomeGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Next Medicine",
                        fontSize = 15.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = nextMedicineName ?: "--",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(260.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nextMedicineDose ?: "--",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = nextMedicineTime ?: "--",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onMarkAsTakenClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = HomeGreen
                        )
                    ) {
                        Text(
                            text = "Mark as Taken",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Today Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Medication,
                    title = "Medicines",
                    valueBold = "3",
                    valueRest = " / 3 Taken"
                )
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.CalendarMonth,
                    title = "Appointments",
                    valueBold = "1",
                    valueRest = " Upcoming"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.WaterDrop,
                    title = "Water Intake",
                    valueBold = "4",
                    valueRest = " / 8 Glasses"
                )
                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.BarChart,
                    title = "Monthly Statistics",
                    valueBold = "Good job! \uD83C\uDF89",
                    valueRest = "",
                    footer = "Keep it up this month."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Encouragement banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = HomeGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Keep it up! You're doing great!",
                    color = HomeGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OverviewCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    valueBold: String,
    valueRest: String,
    footer: String? = null,
    onArrowClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(22.dp)
                )
                IconButton(
                    onClick = onArrowClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View details",
                        tint = Color.DarkGray,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    ) {
                        append(valueBold)
                    }
                    append(valueRest)
                },
                style = MaterialTheme.typography.bodyMedium
            )
            if (footer != null) {
                Text(
                    text = footer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    Medication_DemoTheme {
        HomeScreen(username = "Sarah")
    }
}