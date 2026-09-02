package com.example.medication_demo.navigation

import androidx.compose.ui.platform.LocalContext
import com.example.medication_demo.reminder.AppointmentReminder
import com.example.medication_demo.appointment.AddAppointmentScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import com.example.medication_demo.waterIntake.WaterIntakeScreen
import com.example.medication_demo.appointment.RescheduleAppointmentScreen
import com.example.medication_demo.appointment.EditAppointmentScreen
import com.example.medication_demo.repository.AppointmentRepository
import com.example.medication_demo.model.AppointmentStatus
import com.example.medication_demo.appointment.AppointmentListScreen
import com.example.medication_demo.reminder.RefillReminderScreen
import com.example.medication_demo.statistics.MonthlyStatisticsScreen
import com.example.medication_demo.statistics.MedicationPerformanceScreen
import com.example.medication_demo.statistics.MissedMedicationScreen
import kotlinx.coroutines.delay
import com.example.medication_demo.utils.getMalaysiaTime
import com.example.medication_demo.reminder.showRefillNotification
import androidx.compose.runtime.LaunchedEffect
import com.example.medication_demo.reminder.scheduleRefillReminder
import com.example.medication_demo.history.MedicineHistoryDetailScreen
import java.time.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.medication_demo.appointment.AppointmentDetailsScreen
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.viewmodel.WaterIntakeViewModel
import com.example.medication_demo.viewmodel.WeeklyHistoryViewModel
import kotlinx.coroutines.launch
@Composable
fun MedicationApp(
    onNotificationHandled: () -> Unit = {},
    notificationMedicineId: Int? = null
    ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navController = rememberNavController()
    val medicineVm: MedicineViewModel = viewModel()
    val lowStockMedicineId by medicineVm.lowStockMedicineId.collectAsStateWithLifecycle()
    val medicineListVm: MedicineListViewModel = viewModel()
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val historyVm: WeeklyHistoryViewModel = viewModel()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val appointments by AppointmentRepository.appointments.collectAsStateWithLifecycle()
    val waterVm: WaterIntakeViewModel = viewModel()

    LaunchedEffect(Unit) {
        waterVm.initialize(context.applicationContext)
    }
    val waterUiState by waterVm.uiState.collectAsStateWithLifecycle()

    val upcomingAppointmentCount =
        appointments.count {
            it.status == AppointmentStatus.UPCOMING
        }.toString()

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
        remember(
            medicines, takenRecords, rescheduledDoses, refreshTime
        ) {
            medicineListVm.getNextMedicineDose(
                medicines = medicines,
                takenRecords = takenRecords,
                rescheduledDoses = rescheduledDoses
            )
        }
    val nextMedicineDisplayName =
        remember(medicines, nextDose, takenRecords, rescheduledDoses) {
            medicineListVm.getNextMedicineDisplayName(
                medicines = medicines,
                nextDose = nextDose,
                takenRecords = takenRecords,
                rescheduledDoses = rescheduledDoses
            )
        }
    LaunchedEffect(lowStockMedicineId) {
        val medicineId =
            lowStockMedicineId
                ?: return@LaunchedEffect
        val medicine =
            medicines.find {
                it.id == medicineId
            } ?: return@LaunchedEffect
        val remaining =
            medicineVm
                .getRemainingQuantity(medicine)
                .toInt()
        showRefillNotification(
            context = context,
            medicineId = medicine.id,
            medicineName = medicine.name,
            tabletsLeft = remaining
        )
        navController.navigate(
            "refillReminder/${medicine.id}"
        )
        medicineVm.clearLowStockEvent()
    }
    LaunchedEffect(
        notificationMedicineId
    ) {

        val medicineId =
            notificationMedicineId
                ?: return@LaunchedEffect

        navController.navigate(
            "refillReminder/$medicineId"
        ) {
            launchSingleTop = true
        }

        onNotificationHandled()
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            // Home Screen
            composable("home") {
                HomeScreen(
                    username = "Sarah",
                    nextMedicineName = nextMedicineDisplayName,
                    nextMedicineDose = nextDose?.dosage,
                    nextMedicineTime = nextDose?.reminderTime,
                    nextMedicineStatus = nextDose?.status,
                    waterGlasses = waterUiState.glasses.toString(),
                    waterGoal = waterUiState.dailyGoal,
                    medicinesTaken =
                        if (medicinesTotal == 0) {
                            null
                        } else {
                            medicinesTaken.toString()
                        },

                    medicinesTotal = medicinesTotal,
                    upcomingAppointments = upcomingAppointmentCount,
                    onMedicinesClick = {
                        navController.navigate("medicineSchedule")
                    },
                    onAppointmentClick = {
                        navController.navigate("appointmentList")
                    },
                    onWaterIntakeClick = {
                        navController.navigate("waterIntake")
                    },
                    onMonthlyStatisticsClick = {
                        navController.navigate("monthlyStatistics")
                    },
                    onMarkAsTakenClick = {
                        if (nextDose != null) {
                            medicineVm.markDoseAsTaken(
                                medicineId = nextDose.medicineId,
                                doseIndex = nextDose.doseIndex,
                                reminderTime = nextDose.originalTime
                            )
                        }
                    },
                    onRescheduleConfirm = { newTime ->
                        if (nextDose != null) {
                            medicineVm.rescheduleDose(
                                medicineId = nextDose.medicineId,
                                doseIndex = nextDose.doseIndex,
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
                            3 -> navController.navigate("profile")
                        }
                    }


                )
            }

            // Medicine List Screen
            composable("medicine") {
                MedicineListScreen(
                    medicineVm = medicineVm,
                    listVm = medicineListVm,
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

            // appointment list
            composable("appointmentList") {
                val context = LocalContext.current

                AppointmentListScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddAppointmentClick = {
                        navController.navigate("addAppointment")
                    },
                    onAppointmentClick = {appointment ->
                        navController.navigate(
                            "appointmentDetails/${appointment.id}"
                        )},
                    onNotificationClick = {
                        AppointmentRepository.appointments.value
                            .firstOrNull {
                                it.status == AppointmentStatus.UPCOMING
                            }
                            ?.let { appointment ->
                                AppointmentReminder.showTestNotification(
                                    context = context,
                                    appointment = appointment
                                )
                            }
                    }
                )
            }

            composable("addAppointment") {
                AddAppointmentScreen(
                    onBackClick = {navController.popBackStack() },
                    onSaveSuccess = {navController.popBackStack() }
                )
            }

            composable("appointmentDetails/{appointmentId}") { backStackEntry ->

                val appointmentId = backStackEntry.arguments
                    ?.getString("appointmentId")
                    ?.toIntOrNull()

                if (appointmentId != null) {
                    AppointmentDetailsScreen(
                        appointmentId = appointmentId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onEditClick = {
                            navController.navigate(
                                "editAppointment/$appointmentId"
                            )
                                      },
                        onDeleteSuccess = {
                            navController.popBackStack()
                        },
                        onRescheduleClick = {
                            navController.navigate(
                                "rescheduleAppointment/$appointmentId"
                            )
                        }
                    )
                }
            }

            composable("editAppointment/{appointmentId}") { backStackEntry ->

                val appointmentId = backStackEntry.arguments
                    ?.getString("appointmentId")
                    ?.toIntOrNull()

                if (appointmentId != null) {
                    EditAppointmentScreen(
                        appointmentId = appointmentId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onSaveSuccess = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable("rescheduleAppointment/{appointmentId}") { backStackEntry ->

                val appointmentId = backStackEntry.arguments
                    ?.getString("appointmentId")
                    ?.toIntOrNull()

                if (appointmentId != null) {
                    RescheduleAppointmentScreen(
                        appointmentId = appointmentId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onRescheduleSuccess = {
                            navController.popBackStack(
                                route = "appointmentList",
                                inclusive = false
                            )
                        }
                    )
                }
            }

            composable("waterIntake") {
                WaterIntakeScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    viewModel = waterVm
                )
            }

            composable("monthlyStatistics") {
                MonthlyStatisticsScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onMedicationPerformanceClick = {
                        navController.navigate(
                            "medicationPerformance"
                        )
                    },
                    onMissedMedicationClick = {
                        navController.navigate(
                            "missedMedication"
                        )
                    }
                )
            }

            composable("medicationPerformance") {
                MedicationPerformanceScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("missedMedication") {
                MissedMedicationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Weekly History
            composable("history") {

                WeeklyHistoryScreen(
                    historyVm = historyVm,
                    medicineVm = medicineVm,
                    medicineListVm = medicineListVm,
                    onMedicineClick = { medicineId,
                                        startDate,
                                        endDate ->
                        navController.navigate(
                            "medicineHistory/" +
                                    "$medicineId/" +
                                    "$startDate/" +
                                    "$endDate"
                        )
                    },
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
                        navController.navigate("medicineCalendar")
                    }
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

            // Reminder Refill
            composable(
                route = "refillReminder/{medicineId}"
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
                    val remaining =
                        medicineVm
                            .getRemainingQuantity(medicine)
                            .toInt()
                    RefillReminderScreen(
                        medicineName = medicine.name,
                        tabletsLeft = remaining,
                        presetImageRes = medicine.presetImageRes,
                        galleryImageUri = medicine.galleryImageUri,
                        onRefillConfirm = { refillQuantity ->
                            medicineVm.refillMedicine(
                                medicineId = medicine.id,
                                refillQuantity = refillQuantity
                            )
                            val newRemaining = medicineVm.getRemainingQuantity(medicine)
                            val remainingText =
                                if (newRemaining % 1.0 == 0.0) {
                                    newRemaining.toInt().toString()
                                } else {
                                    newRemaining.toString()
                                }
                            navController.popBackStack()
                            scope.launch {
                                snackbarHostState
                                    .currentSnackbarData
                                    ?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = "Refill successful. Remaining quantity: $remainingText"
                                )
                            }
                        },
                        onRemindAgainConfirm = { minutes ->
                            scheduleRefillReminder(
                                context = context,
                                medicineId = medicine.id,
                                medicineName = medicine.name,
                                tabletsLeft = remaining,
                                delayMinutes = minutes
                            )
                            //showReminderScheduledNotification(
                            //context = context,
                            //minutes = minutes
                            //)
                            navController.popBackStack()
                        }
                    )
                }
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
                    val remainingQuantity =
                        medicineVm.getRemainingQuantity(
                            medicine
                        )
                    MedicineDetailsScreen(
                        medicine = medicine,
                        remainingQuantity = remainingQuantity,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onEditClick = {
                            medicineVm.loadMedicineForEdit(
                                medicine
                            )
                            navController.navigate(
                                "editMedicine/${medicine.id}"
                            )
                        },
                        onTakeNow = {
                            val success =
                                medicineVm.markAsNeededMedicineTaken(
                                    medicineId = medicine.id
                                )

                            if (!success) {
                                scope.launch {
                                    snackbarHostState
                                        .currentSnackbarData
                                        ?.dismiss()

                                    snackbarHostState.showSnackbar(
                                        message =
                                            "This medicine was already taken within the last minute."
                                    )
                                }
                            }
                            navController.popBackStack()
                        },
                        onDeleteClick = {
                            val medicineName = medicine.name
                            medicineVm.deleteMedicine(medicine.id)
                            navController.popBackStack()
                            scope.launch {
                                snackbarHostState
                                    .currentSnackbarData
                                    ?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = "$medicineName was deleted successfully."
                                )
                            }
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

            // History Details
            composable(
                route =
                    "medicineHistory/" +
                            "{medicineId}/" +
                            "{startDate}/" +
                            "{endDate}"
            ) { backStackEntry ->
                val medicineId =
                    backStackEntry.arguments
                        ?.getString("medicineId")
                        ?.toIntOrNull()

                val startDate =
                    backStackEntry.arguments
                        ?.getString("startDate")
                        ?.let {
                            LocalDate.parse(it)
                        }

                val endDate =
                    backStackEntry.arguments
                        ?.getString("endDate")
                        ?.let {
                            LocalDate.parse(it)
                        }
                val activeMedicine =
                    medicines.find {
                        it.id == medicineId
                    }

                val archivedMedicines by medicineVm.archivedMedicines.collectAsStateWithLifecycle()
                val archivedMedicine =
                    archivedMedicines.find {
                        it.medicine.id == medicineId
                    }

                val medicine =
                    activeMedicine
                        ?: archivedMedicine?.medicine

                if (
                    medicine != null &&
                    startDate != null &&
                    endDate != null
                ) {
                    val dailyHistory =
                        medicineListVm
                            .getDailyHistoryForRange(
                                medicine = medicine,
                                startDate = startDate,
                                endDate = endDate,
                                medicineVm = medicineVm,
                                takenRecords = takenRecords,
                                rescheduledDoses =
                                    rescheduledDoses
                            )

                    MedicineHistoryDetailScreen(
                        medicineName = medicine.name,
                        dailyHistory = dailyHistory,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                )
        )
    }
}