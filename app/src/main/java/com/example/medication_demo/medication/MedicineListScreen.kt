package com.example.medication_demo.medication

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medication_demo.R
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.viewmodel.MedicineViewModel
import androidx.compose.material.icons.filled.Schedule
import com.example.medication_demo.viewmodel.MedicineListViewModel

private val AppGreen = Color(0xFF17852B)
private val LightGreen = Color(0xFFE8F5E9)
private val ScreenBackground = Color(0xFFFAFAFA)
private val SoftGrey = Color(0xFFF3F4F6)
private val TextGrey = Color(0xFF6B7280)

data class MedicineUi(
    val name: String,
    val time: String,
    val frequency: String,
    val dosage: String,
    val emoji: String
)

@Composable
fun MedicineListScreen(
    modifier: Modifier = Modifier,
    medicineVm: MedicineViewModel = viewModel(),
    listVm: MedicineListViewModel = viewModel(),
    onAddMedicineClick: () -> Unit = {}
) {
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val searchText by listVm.searchText.collectAsStateWithLifecycle()
    val selectedFilter by listVm.selectedFilter.collectAsStateWithLifecycle()

    var selectedBottomItem by remember {
        mutableIntStateOf(1)
    }
    val filteredMedicines = listVm.filterMedicines(
        medicines = medicines,
        searchText = searchText,
        selectedFilter = selectedFilter
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        bottomBar = {
            MedicationBottomBar(
                selectedIndex = selectedBottomItem,
                onSelected = { selectedBottomItem = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            MedicineTopBar()

            Spacer(modifier = Modifier.height(8.dp))

            MedicineSearchBar(
                value = searchText,
                onValueChange = listVm::onSearchTextChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            MedicineFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = listVm::onFilterSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = filteredMedicines,
                    key = { it.id }
                ) { medicine -> MedicineCard(medicine = medicine)
                }
            }

            Button(
                onClick = onAddMedicineClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppGreen
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Add Medicine",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun MedicineTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "All Medicines",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = {
                // Notification page later
            }
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifications"
            )
        }
    }
}

@Composable
private fun MedicineSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(
                text = "Search medicine",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGrey
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextGrey
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = AppGreen,
            unfocusedIndicatorColor = Color(0xFFE5E7EB)
        )
    )
}

@Composable
private fun MedicineFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "Active", "Upcoming", "Completed")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppGreen,
                    selectedLabelColor = Color.White,
                    containerColor = SoftGrey
                ),
                border = null
            )
        }
    }
}

@Composable
private fun MedicineCard(
    medicine: Medicine
) {
    val dosageText =
        if (medicine.dosageAmount == "1") {
            "${medicine.dosageAmount} ${medicine.dosageType}"
        } else {
            "${medicine.dosageAmount} ${medicine.dosageType}s"
        }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = {
            // Medicine details page later
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = SoftGrey,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = medicine.name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))
                if (medicine.reminderTimes.isNotEmpty()) {
                    medicine.reminderTimes.forEach { reminder ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Reminder time",
                                tint = TextGrey,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = reminder.time,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGrey
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                } else {
                    // For "As needed"
                    Text(
                        text = "No fixed reminder time",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                }
                Spacer(modifier = Modifier.height(5.dp))

                // Dosage + Frequency
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = SoftGrey,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            )
                    ) {
                        Text(
                            text = dosageText,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = medicine.frequency,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGrey
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = AppGreen,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = TextGrey,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

private data class BottomItem(
    val label: String,
    val icon: ImageVector
)

@Composable
private fun MedicationBottomBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomItem("Home", Icons.Default.Home),
        BottomItem("Medicine", Icons.Default.Medication),
        BottomItem("History", Icons.Default.History),
        BottomItem("Profile", Icons.Default.Person)
    )

    Column {
        HorizontalDivider(
            color = Color(0xFFE5E7EB)
        )

        NavigationBar(
            containerColor = Color.White
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        onSelected(index)
                    },
                    icon = {
                        if (item.label == "Medicine") {
                            Icon(
                                painter = painterResource(R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24),
                                contentDescription = item.label
                            )
                        } else {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppGreen,
                        selectedTextColor = AppGreen,
                        indicatorColor = LightGreen,
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey
                    )
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MedicineListScreenPreview() {
    Medication_DemoTheme {
        MedicineListScreen()
    }
}
