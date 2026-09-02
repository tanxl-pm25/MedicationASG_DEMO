package com.example.medication_demo.appointment

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.medication_demo.R
import com.example.medication_demo.model.AppointmentStatus
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.medication_demo.model.AppointmentUi
import com.example.medication_demo.viewmodel.AppointmentListViewModel


private val AppGreen = Color(0xFF16843A)
private val SoftGrey = Color(0xFFF5F5F5)
private val TextGrey = Color(0xFF757575)
private val DarkText = Color(0xFF242424)


@Composable
fun AppointmentListScreen(
    onBackClick: () -> Unit = {},
    onAddAppointmentClick: () -> Unit = {},
    onAppointmentClick: (AppointmentUi) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: AppointmentListViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),

        containerColor = Color.White,

        bottomBar = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
            ) {

                Button(
                    onClick = onAddAppointmentClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppGreen
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Add Appointment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            AppointmentTopBar(
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick,
            )

            Spacer(
                modifier = Modifier.height(26.dp)
            )

            AppointmentTabs(
                isUpcomingSelected = uiState.isUpcomingSelected,
                onUpcomingClick = { viewModel.showUpcoming() },
                onHistoryClick = { viewModel.showHistory() }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text =
                    if (uiState.isUpcomingSelected) {
                        "Upcoming (${uiState.selectedCount})"
                    } else {
                        "History (${uiState.selectedCount})"
                    },

                color = DarkText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (
                uiState.displayedAppointments.isEmpty()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text =
                            if (uiState.isUpcomingSelected) {
                                "No upcoming appointments."
                            } else {
                                "No appointment history yet."
                            },
                        color = TextGrey
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    if (uiState.isUpcomingSelected) {
                        items(uiState.upcomingAppointments) { appointment ->
                            AppointmentItem(
                                appointment = appointment,
                                showStatus = false,
                                onClick = {
                                    onAppointmentClick(appointment)
                                }
                            )

                            HorizontalDivider(
                                color = Color(0xFFF0F0F0)
                            )
                        }
                    } else {
                        if (uiState.actionNeededAppointments.isNotEmpty()) {
                            item {
                                HistorySectionTitle(
                                    text = "Action Needed (${uiState.actionNeededAppointments.size})"
                                )
                            }

                            items(uiState.actionNeededAppointments) { appointment ->
                                AppointmentItem(
                                    appointment = appointment,
                                    showStatus = true,
                                    onClick = {
                                        onAppointmentClick(appointment)
                                    }
                                )

                                HorizontalDivider(
                                    color = Color(0xFFF0F0F0)
                                )
                            }
                        }

                        if (uiState.resolvedAppointments.isNotEmpty()) {
                            item {
                                HistorySectionTitle(
                                    text = "Completed & Rescheduled (${uiState.resolvedAppointments.size})"
                                )
                            }

                            items(uiState.resolvedAppointments) { appointment ->
                                AppointmentItem(
                                    appointment = appointment,
                                    showStatus = true,
                                    onClick = {
                                        onAppointmentClick(appointment)
                                    }
                                )

                                HorizontalDivider(
                                    color = Color(0xFFF0F0F0)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AppointmentTopBar(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        contentAlignment = Alignment.Center
    ) {

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription = "Back",
                tint = DarkText,
                modifier = Modifier.size(24.dp)
            )
        }


        Text(
            text = "Appointments",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )


        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .align(
                    Alignment.CenterEnd
                )
        ) {

            Icon(
                imageVector =
                    Icons.Default.NotificationsNone,

                contentDescription =
                    "Notifications",

                tint = DarkText,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}


@Composable
private fun AppointmentTabs(
    isUpcomingSelected: Boolean,
    onUpcomingClick: () -> Unit,
    onHistoryClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        AppointmentTab(
            text = "Upcoming",
            isSelected = isUpcomingSelected,
            onClick = onUpcomingClick,
            modifier = Modifier.weight(1f)
        )


        AppointmentTab(
            text = "History",
            isSelected = !isUpcomingSelected,
            onClick = onHistoryClick,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun AppointmentTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .height(46.dp)
            .background(
                color =
                    if (isSelected) {
                        AppGreen
                    } else {
                        SoftGrey
                    },

                shape =
                    RoundedCornerShape(
                        10.dp
                    )
            )
            .clickable(
                onClick = onClick
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,

            color =
                if (isSelected) {
                    Color.White
                } else {
                    TextGrey
                },

            fontSize = 15.sp,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}


@Composable
private fun AppointmentItem(
    appointment: AppointmentUi,
    showStatus: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = doctorAvatarFor(appointment.doctor)
                ),
                contentDescription = "Doctor photo",
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.doctor,
                    color = DarkText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = appointment.appointmentName,
                    color = TextGrey,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = appointment.date,
                        color = TextGrey,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.width(25.dp))

                    Text(
                        text = appointment.time,
                        color = TextGrey,
                        fontSize = 13.sp
                    )
                }

                if (showStatus) {
                    Spacer(modifier = Modifier.height(9.dp))
                    AppointmentStatusBadge(status = appointment.status)
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View appointment details",
                tint = TextGrey
            )
        }
    }
}

@Composable
private fun AppointmentStatusBadge(
    status: AppointmentStatus
) {
    val (label, backgroundColor, textColor) = when (status) {
        AppointmentStatus.COMPLETED -> Triple(
            "Completed",
            Color(0xFFE6F4EA),
            AppGreen
        )

        AppointmentStatus.MISSED -> Triple(
            "Missed",
            Color(0xFFFFE9E7),
            Color(0xFFC62828)
        )

        AppointmentStatus.CANCELLED -> Triple(
            "Cancelled",
            Color(0xFFF0F0F0),
            TextGrey
        )

        AppointmentStatus.UPCOMING -> Triple(
            "Upcoming",
            Color(0xFFF0F0F0),
            TextGrey
        )

        AppointmentStatus.RESCHEDULED -> Triple(
            "Rescheduled",
            Color(0xFFE8F0FE),
            Color(0xFF2563EB)
        )
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
private fun HistorySectionTitle(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(
            top = 14.dp,
            bottom = 6.dp
        ),
        color = DarkText,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}

private fun doctorAvatarFor(
    doctorName: String
): Int {
    return if (
        (doctorName.hashCode() and 1) == 0
    ) {
        R.drawable.doctor_male
    } else {
        R.drawable.doctor_female
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AppointmentListScreenPreview() {

    MaterialTheme {
        AppointmentListScreen()
    }
}