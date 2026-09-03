package com.example.medication_demo.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medication_demo.model.Medicine
import com.example.medication_demo.viewmodel.MedicineViewModel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.example.medication_demo.components.MainScreenTopBar
import com.example.medication_demo.viewmodel.AppBottomNavigationBar
import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.model.MedicineStatus

private val AppGreen = Color(0xFF17852B)
private val SoftGrey = Color(0xFFF3F4F6)
private val TextGrey = Color(0xFF6B7280)

@Composable
fun MedicineListScreen(
    modifier: Modifier = Modifier,
    medicineVm: MedicineViewModel = viewModel(),
    listVm: MedicineListViewModel = viewModel(),
    onAddMedicineClick: () -> Unit = {},
    onMedicineClick: (Int) -> Unit = {},
    onBottomNavSelected: (Int) -> Unit = {}
) {
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val searchText by listVm.searchText.collectAsStateWithLifecycle()
    val selectedFilter by listVm.selectedFilter.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val filteredMedicines = listVm.filterMedicines(
        medicines = medicines,
        searchText = searchText,
        selectedFilter = selectedFilter
    )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomNavigationBar(
                selectedIndex = 1,
                onSelected = onBottomNavSelected
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            MainScreenTopBar(
                title = "All Medicines",
                rightIcon = Icons.Default.NotificationsNone,
                rightIconDescription = "Notifications",
                onRightIconClick = {
                    // Notification page later
                },
                modifier = Modifier.padding(
                    start = 5.dp
                ),
                titleStartPadding = 5.dp
            )

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

            if (filteredMedicines.isEmpty()) {

                val emptyMessage =
                    when {
                        searchText.isNotBlank() -> {
                            "No medicine found"
                        }
                        medicines.isEmpty() -> {
                            "No medicines added yet"
                        }
                        selectedFilter != "All" -> {
                            "No ${selectedFilter.lowercase()} medicines"
                        }
                        else -> {
                            "No medicines added yet"
                        }
                    }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGrey
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        bottom = 10.dp
                    )
                ) {
                    items(
                        items = filteredMedicines,
                        key = { it.id }
                    ) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            dosageText = listVm.getDosageText(medicine),
                            status = listVm.getMedicineStatus(medicine),
                            onClick = {
                                onMedicineClick(medicine.id)
                            }
                        )
                    }
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
        horizontalArrangement = Arrangement.spacedBy(9.dp)
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
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
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
    medicine: Medicine,
    dosageText: String,
    status: MedicineStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
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
                MedicineImage(
                    presetImageRes = medicine.presetImageRes,
                    galleryImageUri = medicine.galleryImageUri,
                    contentDescription = medicine.name,
                    imageSize = 46.dp
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleSmall
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

            when (status) {
                MedicineStatus.ACTIVE -> {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = SoftGrey,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Active",
                            tint = TextGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                MedicineStatus.UPCOMING -> {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = SoftGrey,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Upcoming",
                            tint = TextGrey,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                MedicineStatus.COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = AppGreen,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
