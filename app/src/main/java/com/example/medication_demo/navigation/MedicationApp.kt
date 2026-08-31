package com.example.medication_demo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medication_demo.history.WeeklyHistoryScreen
import com.example.medication_demo.medication.AddMedicineScreen
import com.example.medication_demo.medication.MedicineDetailsScreen
import com.example.medication_demo.medication.MedicineListScreen
import com.example.medication_demo.user.HomeScreen
import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.viewmodel.MedicineViewModel
import com.example.medication_demo.medication.MedicineScheduleScreen
import com.example.medication_demo.medication.MedicineCalendarScreen
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import com.example.medication_demo.utils.getMalaysiaTime

@Composable
fun MedicationApp() {

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val navController = rememberNavController()
    val medicineVm: MedicineViewModel = viewModel()
    val medicineListVm: MedicineListViewModel = viewModel()
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val medicinesTotal =
        medicineListVm.getTodayTotalDoseCount(
            medicines = medicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses
        )
    val medicinesTaken =
        medicineListVm.getTodayTakenCount(
            medicines = medicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses
        )
    val refreshTime by produceState(
        initialValue = getMalaysiaTime()
    ) {
        while (true) {
            value = getMalaysiaTime()
            delay(1000)
        }
    }
    val nextDose =
        remember(medicines, takenRecords, rescheduledDoses, refreshTime
        ) {
            medicineListVm.getNextMedicineDose(
                medicines = medicines,
                takenRecords = takenRecords,
                rescheduledDoses = rescheduledDoses
            )
        }
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Home Screen
        composable("home") {
            HomeScreen(
                username = "Sarah",
                nextMedicineName = nextDose?.medicineName,
                nextMedicineDose = nextDose?.dosage,
                nextMedicineTime = nextDose?.reminderTime,
                nextMedicineStatus = nextDose?.status,
                medicinesTaken =
                    if (medicinesTotal == 0) {
                        null
                    } else {
                        medicinesTaken.toString()
                    },

                medicinesTotal = medicinesTotal,
                onMedicinesClick = {
                    navController.navigate("medicineSchedule")
                },
                onMarkAsTakenClick = {
                    if (nextDose != null) {
                        medicineVm.markDoseAsTaken(
                            medicineId = nextDose.medicineId,
                            reminderTime = nextDose.originalTime
                        )
                    }
                },
                onRescheduleConfirm = { newTime ->
                    if (nextDose != null) {
                        medicineVm.rescheduleDose(
                            medicineId = nextDose.medicineId,
                            originalTime = nextDose.originalTime,
                            newTime = newTime
                        )
                    }
                },
                onBottomNavSelected = { index ->
                    when (index) {
                        0 -> {
                            // Already on home
                        }
                        1 -> navController.navigate("medicine")
                        2 -> navController.navigate("history")
                        3 -> {
                            // Profile later
                        }
                    }
                }
            )
        }

        // Medicine List Screen
        composable("medicine") {
            MedicineListScreen(
                medicineVm = medicineVm,
                listVm = medicineListVm,
                snackbarMessage = snackbarMessage,
                onSnackbarShown = {
                    snackbarMessage = null
                },
                onAddMedicineClick = {
                    navController.navigate("addMedicine")
                },
                onMedicineClick = { medicineId ->
                    navController.navigate(
                        "medicineDetails/$medicineId"
                    )
                },
                onBottomNavSelected = { index ->
                    when (index) {
                        0 -> {
                            navController.navigate("home")
                        }
                        1 -> {
                            // Already on Medicine
                        }
                        2 -> {
                            navController.navigate("history")
                        }
                        3 -> {
                            // Profile later
                        }
                    }
                }
            )
        }

        // Weekly History
        composable("history") {

            WeeklyHistoryScreen(
                medicineVm = medicineVm,
                medicineListVm = medicineListVm,
                onBottomNavSelected = { index ->

                    when (index) {
                        0 -> {
                            navController.navigate("home")
                        }
                        1 -> {
                            navController.navigate("medicine")
                        }
                        2 -> {
                            // Already on History
                        }
                        3 -> {
                            // Profile later
                        }
                    }
                }
            )
        }

        // Medicine Schedule
        composable("medicineSchedule") {
            MedicineScheduleScreen(
                medicineVm = medicineVm,
                medicineListVm = medicineListVm,
                onBackClick = {
                    navController.popBackStack()
                },

                onViewCalendarClick = {
                    navController.navigate("medicineCalendar")                }
            )
        }

        // Calendar
        composable("medicineCalendar") {
            MedicineCalendarScreen(
                medicineVm = medicineVm,
                medicineListVm = medicineListVm,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Add Medicine
        composable("addMedicine") {
            AddMedicineScreen(
                isEditMode = false,
                vm = medicineVm,

                onBackClick = {
                    medicineVm.resetAddMedicineForm()
                    navController.popBackStack()
                },

                onSaveClick = {
                    medicineVm.resetAddMedicineForm()
                    navController.popBackStack()
                }
            )
        }

        // Medicine Details
        composable(
            route = "medicineDetails/{medicineId}"
        ) { backStackEntry ->

            val medicineId =
                backStackEntry.arguments
                    ?.getString("medicineId")
                    ?.toIntOrNull()

            val medicine =
                medicines.find {
                    it.id == medicineId
                }

            if (medicine != null) {
                MedicineDetailsScreen(
                    medicine = medicine,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = {
                        medicineVm.loadMedicineForEdit(medicine)

                        navController.navigate(
                            "editMedicine/${medicine.id}"
                        )
                    },
                    onTakeNow = {
                        medicineVm.markAsNeededMedicineTaken(
                            medicineId = medicine.id
                        )
                        navController.popBackStack()
                    },
                    onDeleteClick = {
                        medicineVm.deleteMedicine(medicine.id)
                        snackbarMessage = "${medicine.name} was deleted successfully."
                        navController.popBackStack()
                    },
                    onReminderChanged = { enabled ->
                        medicineVm.updateReminderEnabled(
                            id = medicine.id,
                            enabled = enabled
                        )
                    }
                )
            }
        }

        // Edit Medicine
        composable(
            route = "editMedicine/{medicineId}"
        ) { backStackEntry ->

            val medicineId =
                backStackEntry.arguments
                    ?.getString("medicineId")
                    ?.toIntOrNull()

            AddMedicineScreen(
                isEditMode = true,
                medicineId = medicineId,
                vm = medicineVm,
                onBackClick = {
                    medicineVm.resetAddMedicineForm()
                    navController.popBackStack()
                },
                onSaveClick = {
                    medicineVm.resetAddMedicineForm()
                    navController.popBackStack()
                }
            )
        }
    }
}