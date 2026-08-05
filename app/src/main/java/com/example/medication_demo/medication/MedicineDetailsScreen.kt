package com.example.medication_demo.medication

import com.example.medication_demo.ui.theme.Medication_DemoTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.R
import androidx.compose.ui.text.style.TextAlign

private val DetailGreen = Color(0xFF159447)
private val DetailLightGreen = Color(0xFFE8F7ED)
private val DetailRed = Color(0xFFFF3B30)
private val DetailLightRed = Color(0xFFFFEEEE)
private val DetailGrey = Color(0xFF6B7280)
private val DetailDivider = Color(0xFFE5E7EB)
private val DetailBackground = Color(0xFFFFFFFF)

@Composable
fun MedicineDetailsScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var reminderEnabled by remember {
        mutableStateOf(true)
    }

    Scaffold(
        containerColor = DetailBackground,
        topBar = {
            MedicineDetailsTopBar(
                onBackClick = onBackClick,
                onMoreClick = onMoreClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MedicineSummary()

            Spacer(modifier = Modifier.height(24.dp))

            MedicineActionButtons(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = DetailDivider
            )

            Spacer(modifier = Modifier.height(18.dp))

            MedicineInformationSection()

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = DetailDivider
            )

            Spacer(modifier = Modifier.height(10.dp))

            ReminderSection(
                reminderEnabled = reminderEnabled,
                onReminderChanged = {
                    reminderEnabled = it
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MedicineDetailsTopBar(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 10.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Medicine Details",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = onMoreClick
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun MedicineSummary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F3F3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                ),
                contentDescription = "Medicine",
                tint = Color.Unspecified,
                modifier = Modifier.size(58.dp)
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Metformin",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "1 Tablet • Twice a day",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "10:00 AM, 08:00 PM",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DetailLightGreen)
                    .padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                color = DetailGreen,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.labelMedium,
                        color = DetailGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicineActionButtons(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        MedicineActionButton(
            modifier = Modifier.weight(1f),
            text = "Edit",
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = DetailGreen,
                    modifier = Modifier.size(25.dp)
                )
            },
            borderColor = Color(0xFFB7E3C4),
            backgroundColor = Color.White,
            textColor = DetailGreen,
            onClick = onEditClick
        )

        MedicineActionButton(
            modifier = Modifier.weight(1f),
            text = "Delete",
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = DetailRed,
                    modifier = Modifier.size(25.dp)
                )
            },
            borderColor = Color(0xFFFFC5C5),
            backgroundColor = Color.White,
            textColor = DetailRed,
            onClick = onDeleteClick
        )
    }
}

@Composable
private fun MedicineActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable () -> Unit,
    borderColor: Color,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(78.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}

@Composable
private fun MedicineInformationSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Medicine Info",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        MedicineInformationRow(
            label = "Start Date",
            value = "10 May 2025"
        )

        MedicineInformationRow(
            label = "Frequency",
            value = "Twice a day"
        )

        MedicineInformationRow(
            label = "Quantity",
            value = "30 Tablets"
        )

        MedicineInformationRow(
            label = "Notes",
            value = "Take after meal"
        )
    }
}

@Composable
private fun MedicineInformationRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ReminderSection(
    reminderEnabled: Boolean,
    onReminderChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Reminder",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFAFAFA)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DetailLightGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = DetailGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Medicine Reminder",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = "Receive reminders on time",
                            style = MaterialTheme.typography.bodySmall,
                            color = DetailGrey
                        )
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = onReminderChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DetailGreen
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DetailDivider
                )

                ReminderInformationRow(
                    title = "Reminder time",
                    value = "10:00 AM, 08:00 PM"
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DetailDivider
                )

                ReminderInformationRow(
                    title = "Repeat",
                    value = "Every day"
                )
            }
        }
    }
}

@Composable
private fun ReminderInformationRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = DetailGreen,
            modifier = Modifier.size(21.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = DetailGrey
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MedicineDetailsScreenPreview() {
    Medication_DemoTheme {
        MedicineDetailsScreen()
    }
}