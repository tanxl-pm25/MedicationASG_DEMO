package com.example.medication_demo.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medication_demo.appointment.AddAppointmentScreen
import com.example.medication_demo.appointment.AppointmentDetailsScreen
import com.example.medication_demo.appointment.AppointmentListScreen
import com.example.medication_demo.appointment.EditAppointmentScreen
import com.example.medication_demo.appointment.RescheduleAppointmentScreen
import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.history.MedicineHistoryDetailScreen
import com.example.medication_demo.history.WeeklyHistoryScreen
import com.example.medication_demo.medication.AddMedicineScreen
import com.example.medication_demo.medication.MedicineCalendarScreen
import com.example.medication_demo.medication.MedicineDetailsScreen
import com.example.medication_demo.medication.MedicineListScreen
import com.example.medication_demo.medication.MedicineScheduleScreen
import com.example.medication_demo.reminder.MedicationNotification
import com.example.medication_demo.reminder.MedicationReminderScreen
import com.example.medication_demo.reminder.TakenDoseScreen
import com.example.medication_demo.reminder.MedicationRescheduleScreen
import com.example.medication_demo.viewmodel.RescheduleMedicationViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.medication_demo.model.AppointmentStatus
import com.example.medication_demo.notification.NotificationHelper
import com.example.medication_demo.reminder.AppointmentReminder
import com.example.medication_demo.reminder.RefillReminderScreen
import com.example.medication_demo.reminder.scheduleRefillReminder
import com.example.medication_demo.reminder.showRefillNotification
import com.example.medication_demo.repository.AppointmentRepository
import com.example.medication_demo.statistics.MedicationPerformanceScreen
import com.example.medication_demo.statistics.MissedMedicationScreen
import com.example.medication_demo.statistics.MonthlyStatisticsScreen
import com.example.medication_demo.user.AgeScreen
import com.example.medication_demo.user.CreateAccountScreen
import com.example.medication_demo.user.EmailVerificationScreen
import com.example.medication_demo.user.ForgotPasswordScreen
import com.example.medication_demo.user.GenderScreen
import com.example.medication_demo.user.HomeScreen
import com.example.medication_demo.user.LoginScreen
import com.example.medication_demo.user.PersonalInfoScreen
import com.example.medication_demo.user.ProfileScreen
import com.example.medication_demo.user.ResetPasswordScreen
import com.example.medication_demo.user.SettingScreen
import com.example.medication_demo.user.SplashScreen
import com.example.medication_demo.utils.getMalaysiaDate
import com.example.medication_demo.utils.getMalaysiaTime

import androidx.compose.foundation.layout.Box

import com.example.medication_demo.viewmodel.MedicineListViewModel
import com.example.medication_demo.viewmodel.MedicineViewModel
import com.example.medication_demo.viewmodel.MonthlyStatisticsViewModel

import com.example.medication_demo.reminder.showRefillNotification
import com.example.medication_demo.reminder.scheduleRefillReminder
import com.example.medication_demo.history.MedicineHistoryDetailScreen
import java.time.LocalDate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.example.medication_demo.reminder.createRefillNotificationChannel
import com.example.medication_demo.viewmodel.MedicationPerformanceViewModel
import com.example.medication_demo.viewmodel.MissedMedicationViewModel
import com.example.medication_demo.waterIntake.WaterIntakeScreen
import com.example.medication_demo.viewmodel.UserViewModel
import com.example.medication_demo.viewmodel.WaterIntakeViewModel
import com.example.medication_demo.viewmodel.WeeklyHistoryViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MedicationApp(
    pendingDeepLinkType: String?,
    onDeepLinkConsumed: () -> Unit,
    onNotificationHandled: () -> Unit = {},
    notificationMedicineId: Int? = null,
    navigateToHomeFromNotification: Boolean = false,
    onHomeNavigationHandled: () -> Unit = {},
    medicationNotificationAction: String? = null,
    medicationNotificationMedicineId: Int? = null,
    medicationNotificationDoseIndex: Int? = null,
    medicationNotificationOriginalTime: String? = null,
    onMedicationNotificationHandled: () -> Unit = {}

    ) {
    // 通知权限的请求器(Android 13以上才需要真的跳出来问)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )
    val userVm: UserViewModel = viewModel()
    val currentUserName by userVm.userName.collectAsStateWithLifecycle()
    val currentUserEmail by userVm.userEmail.collectAsStateWithLifecycle()
    val currentUserGender by userVm.userGender.collectAsStateWithLifecycle()
    val currentUserAge by userVm.userAge.collectAsStateWithLifecycle()
    val latestDeepLinkType = rememberUpdatedState(pendingDeepLinkType)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navController = rememberNavController()
    val medicineVm: MedicineViewModel = viewModel()
    val lowStockMedicineId by medicineVm.lowStockMedicineId.collectAsStateWithLifecycle()
    val insufficientStockMedicineId by medicineVm.insufficientStockMedicineId.collectAsStateWithLifecycle()
    val medicineListVm: MedicineListViewModel = viewModel()
    val medicines by medicineVm.medicines.collectAsStateWithLifecycle()
    val historyVm: WeeklyHistoryViewModel = viewModel()
    val takenRecords by medicineVm.takenRecords.collectAsStateWithLifecycle()
    val missedRecords by medicineVm.missedRecords.collectAsStateWithLifecycle()
    val archivedMedicines by medicineVm.archivedMedicines.collectAsStateWithLifecycle()
    val rescheduledDoses by medicineVm.rescheduledDoses.collectAsStateWithLifecycle()
    val appointments by AppointmentRepository.appointments.collectAsStateWithLifecycle()
    val waterVm: WaterIntakeViewModel = viewModel()

    LaunchedEffect(
        medicationNotificationAction,
        medicationNotificationMedicineId,
        medicationNotificationDoseIndex,
        medicationNotificationOriginalTime,
        medicines
    ) {
        val action =
            medicationNotificationAction
                ?: return@LaunchedEffect

        val medicineId =
            medicationNotificationMedicineId
                ?: return@LaunchedEffect

        val doseIndex =
            medicationNotificationDoseIndex
                ?: return@LaunchedEffect

        val originalTime =
            medicationNotificationOriginalTime
                ?: return@LaunchedEffect

        val medicine = medicines.firstOrNull {
            it.id == medicineId
        }

        if (medicine == null) {
            onMedicationNotificationHandled()
            return@LaunchedEffect
        }

        when (action) {
            MedicationNotification.ACTION_TAKEN -> {
                medicineVm.markDoseAsTaken(
                    medicineId = medicineId,
                    doseIndex = doseIndex,
                    reminderTime = originalTime
                )

                navController.navigate(
                    "takenDose/$medicineId/$doseIndex"
                ) {
                    launchSingleTop = true
                }
            }

            MedicationNotification.ACTION_RESCHEDULE -> {
                navController.navigate(
                    "rescheduleMedication/$medicineId/$doseIndex"
                ) {
                    launchSingleTop = true
                }
            }

            MedicationNotification.ACTION_OPEN_REMINDER -> {
                navController.navigate(
                    "medicationReminder/$medicineId/$doseIndex"
                ) {
                    launchSingleTop = true
                }
            }
        }

        onMedicationNotificationHandled()
    }

    LaunchedEffect(Unit) {
        waterVm.initialize(context.applicationContext)
    }
    val waterUiState by waterVm.uiState.collectAsStateWithLifecycle()
    val todayWaterGlasses = waterVm.getTodayGlasses()

    val upcomingAppointmentCount =
        appointments.count {
            it.status == AppointmentStatus.UPCOMING
        }.toString()

    val medicinesTotal =
        medicineListVm.getTodayTotalDoseCount(
            medicines = medicines,
            archivedMedicines = archivedMedicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses,
            medicineVm = medicineVm
        )
    val medicinesTaken =
        medicineListVm.getTodayTakenCount(
            medicines = medicines,
            archivedMedicines = archivedMedicines,
            takenRecords = takenRecords,
            rescheduledDoses = rescheduledDoses,
            medicineVm = medicineVm
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
        remember(nextDose,medicinesTotal) {
            medicineListVm.getNextMedicineDisplayName(
                nextDose = nextDose,
                todayTotalDoseCount = medicinesTotal
            )
        }
    LaunchedEffect(
        navigateToHomeFromNotification
    ) {
        if (navigateToHomeFromNotification) {
            navController.navigate(
                "home"
            ) {
                popUpTo("home") {
                    inclusive = false
                }
                launchSingleTop = true
            }
            onHomeNavigationHandled()
        }
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
        insufficientStockMedicineId
    ) {
        val medicineId =
            insufficientStockMedicineId
                ?: return@LaunchedEffect
        val medicine =
            medicines.find {
                it.id == medicineId
            }
        if (medicine != null) {
            snackbarHostState.showSnackbar(
                message =
                    "${medicine.name} stock is insufficient for the next dose. Please restock."
            )
        }

        medicineVm.clearInsufficientStockEvent()
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
    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)

        createRefillNotificationChannel(context)
        createMedicineNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // 监听Supabase的登入状态 —— Google登入/密码重设都是浏览器跳转的,
    // 没有明确的"onSuccess"时间点,要靠这个才知道什么时候该跳去哪个页面
    LaunchedEffect(Unit) {
        SupabaseClientProvider.client.auth.sessionStatus.collect { status ->
            if (status is SessionStatus.Authenticated) {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    medicineVm.switchUser(userId)
                }
                if (latestDeepLinkType.value == "recovery") {
                    // 是密码重设连结跳回来的,导去设新密码的页面
                    navController.navigate("resetPassword") {
                        popUpTo(0) { inclusive = true }
                    }
                    onDeepLinkConsumed()
                } else {
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    val authFlowRoutes = setOf(
                        "splash", "login", "createAccount",
                        "forgotPassword", "emailVerification", "gender", "age"
                    )
                    if (currentRoute == null || currentRoute in authFlowRoutes) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            // Splash Screen
            composable("splash") {
                SplashScreen(
                    onGetStartedClick = {
                        navController.navigate("login")
                    }
                )
            }

            // Login Screen
            composable("login") {
                val loginErrorMessage by userVm.errorMessage.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    userVm.clearErrorMessage()
                }

                LoginScreen(
                    errorMessage = loginErrorMessage,
                    onLoginClick = { email, password ->
                        userVm.login(
                            email = email,
                            password = password,
                            onSuccess = {
                                val userId =
                                    SupabaseClientProvider.client.auth
                                        .currentUserOrNull()
                                        ?.id
                                if (userId != null) {
                                    medicineVm.switchUser(userId)
                                }
                                navController.navigate("home") {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    },
                    onForgotPasswordClick = {
                        navController.navigate("forgotPassword")
                    },
                    onGoogleLoginClick = {
                        userVm.loginWithGoogle()
                    },
                    onSignUpClick = {
                        navController.navigate("createAccount")
                    }
                )
            }

            // Create Account Screen
            composable("createAccount") {
                val signUpErrorMessage by userVm.errorMessage.collectAsStateWithLifecycle()
                val isSignUpLoading by userVm.isLoading.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    userVm.clearErrorMessage()
                }

                CreateAccountScreen(
                    errorMessage = signUpErrorMessage,
                    isLoading = isSignUpLoading,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSignUpClick = { name, email, password, _ ->
                        userVm.signUp(
                            name = name,
                            email = email,
                            password = password,
                            onSuccess = {
                                NotificationHelper.showVerificationSentNotification(
                                    context = context,
                                    email = email
                                )
                                navController.navigate("emailVerification")
                            }
                        )
                    },
                    onGoogleClick = {
                        userVm.loginWithGoogle()
                    },
                    onLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Forgot Password Screen
            composable("forgotPassword") {
                val forgotPasswordErrorMessage by userVm.errorMessage.collectAsStateWithLifecycle()
                var isResetEmailSent by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    userVm.clearErrorMessage()
                }

                ForgotPasswordScreen(
                    errorMessage = forgotPasswordErrorMessage,
                    isEmailSent = isResetEmailSent,
                    onSendResetLinkClick = { email ->
                        userVm.sendPasswordResetEmail(
                            email = email,
                            onSuccess = {
                                isResetEmailSent = true
                            }
                        )
                    },
                    onBackToLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Reset Password Screen (用户点了email连结跳回app之后设新密码)
            composable("resetPassword") {
                val resetPasswordErrorMessage by userVm.errorMessage.collectAsStateWithLifecycle()
                var isResetSuccess by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    userVm.clearErrorMessage()
                }

                ResetPasswordScreen(
                    errorMessage = resetPasswordErrorMessage,
                    isSuccess = isResetSuccess,
                    onResetPasswordClick = { newPassword ->
                        userVm.updatePassword(
                            newPassword = newPassword,
                            onSuccess = {
                                isResetSuccess = true
                            }
                        )
                    },
                    onBackToLoginClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Email Verification Screen
            composable("emailVerification") {
                val verifyErrorMessage by userVm.errorMessage.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    userVm.clearErrorMessage()
                }

                EmailVerificationScreen(
                    email = currentUserEmail.ifBlank { "example@gmail.com" },
                    errorMessage = verifyErrorMessage,
                    onVerifyClick = { code ->
                        userVm.verifyEmail(
                            email = currentUserEmail,
                            code = code,
                            onSuccess = {
                                navController.navigate("gender")
                            }
                        )
                    },
                    onResendClick = {
                        userVm.resendVerificationCode(currentUserEmail)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Onboarding - Gender
            composable("gender") {
                GenderScreen(
                    onNextClick = { gender ->
                        userVm.onGenderSelected(gender)
                        navController.navigate("age")
                    }
                )
            }

            // Onboarding - Age
            composable("age") {
                AgeScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNextClick = { age ->
                        userVm.onAgeSelected(age)
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            // Profile Screen
            composable("profile") {
                ProfileScreen(
                    name = currentUserName.ifBlank { "User" },
                    email = currentUserEmail.ifBlank { "--" },
                    onPersonalInfoClick = {
                        navController.navigate("personalInfo")
                    },
                    onEmergencyContactsClick = {
                        // TODO: Emergency Contacts screen还没做
                    },
                    onRemindersClick = {
                        // TODO: Reminders设置还没做
                    },
                    onPreferencesClick = {
                        navController.navigate("settings")
                    },
                    onHelpSupportClick = {
                        // TODO: Help & Support还没做
                    },
                    onLogoutClick = {
                        medicineVm.prepareForLogout()
                        userVm.logout()
                        navController.navigate("login") {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },
                    onBottomNavSelected = { index ->
                        navigateBottomBar(
                            index = index,
                            navController = navController
                        )
                    }
                )
            }

            // Settings Screen
            composable("settings")
            {
                SettingScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Personal Info Screen
            composable("personalInfo") {
                PersonalInfoScreen(
                    name = currentUserName.ifBlank { "User" },
                    email = currentUserEmail.ifBlank { "--" },
                    gender = currentUserGender,
                    age = currentUserAge,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPhotoClick = {
                        // TODO: 之后接换头像功能
                    },
                    onNameClick = {
                        // TODO: 之后接改username功能
                    },
                    onEmailClick = {
                        // TODO: 之后接改email功能
                    },
                    onGenderChange = { gender ->
                        userVm.updateGender(gender)
                    },
                    onAgeChange = { age ->
                        userVm.updateAge(age)
                    }
                )
            }

            // Home Screen
            composable("home") {
                HomeScreen(
                    username = "Sarah",
                    nextMedicineName = nextMedicineDisplayName,
                    nextMedicineDose = nextDose?.dosage,
                    nextMedicineTime = nextDose?.reminderTime,
                    nextMedicineStatus = nextDose?.status,
                    waterGlasses = todayWaterGlasses.toString(),
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
                        waterVm.selectToday()
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
                        navigateBottomBar(
                            index = index,
                            navController = navController
                        )
                    }


                )
            }

            composable(
                route = "takenDose/{medicineId}/{doseIndex}"
            ) { backStackEntry ->

                val medicineId = backStackEntry.arguments
                    ?.getString("medicineId")
                    ?.toIntOrNull()

                val doseIndex = backStackEntry.arguments
                    ?.getString("doseIndex")
                    ?.toIntOrNull()

                val medicine = medicines.firstOrNull {
                    it.id == medicineId
                }

                val takenRecord = takenRecords.firstOrNull {
                    it.medicineId == medicineId &&
                            it.doseIndex == doseIndex &&
                            it.date == getMalaysiaDate()
                }

                if (
                    medicine != null &&
                    takenRecord != null
                ) {
                    TakenDoseScreen(
                        medicineName = medicine.name,
                        dosage =
                            "${takenRecord.dosageAmount} " +
                                    takenRecord.dosageType,
                        scheduledTime = takenRecord.reminderTime,
                        takenTime = takenRecord.takenTime.orEmpty(),
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onDoneClick = {
                            navController.navigate("home") {
                                popUpTo("home") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable("rescheduleMedication/{medicineId}/{doseIndex}") { backStackEntry ->

                val medicineId =
                    backStackEntry.arguments
                        ?.getString("medicineId")
                        ?.toIntOrNull()

                val doseIndex =
                    backStackEntry.arguments
                        ?.getString("doseIndex")
                        ?.toIntOrNull()

                val medicine = medicines.firstOrNull {
                    it.id == medicineId
                }

                val rescheduleVm: RescheduleMedicationViewModel =
                    viewModel()

                val rescheduleUiState by rescheduleVm.uiState
                    .collectAsStateWithLifecycle()


                if (medicine != null && doseIndex != null) {
                    val originalTime = medicine.reminderTimes
                        .getOrNull(doseIndex)
                        ?.time
                        ?: "-"

                    val rescheduleDateText =
                        "Today, " + getMalaysiaDate().format(
                            DateTimeFormatter.ofPattern(
                                "dd MMM yyyy",
                                Locale.ENGLISH
                            )
                        )

                    MedicationRescheduleScreen(
                        medicineName = medicine.name,
                        missedTime = originalTime,
                        newTime = rescheduleUiState.newTime.ifBlank { "Select time" },
                        rescheduleDate = rescheduleDateText,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onTimeChange = { time ->
                            rescheduleVm.updateTime(time)
                        },
                        onConfirmClick = {
                            val newTime = rescheduleUiState.newTime

                            when {
                                newTime.isBlank() -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Please select a new time."
                                        )
                                    }
                                }

                                newTime == originalTime -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Please select a different time."
                                        )
                                    }
                                }

                                else -> {
                                    medicineVm.rescheduleDose(
                                        medicineId = medicine.id,
                                        doseIndex = doseIndex,
                                        originalTime = originalTime,
                                        newTime = newTime
                                    )

                                    rescheduleVm.reset()

                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },

                        onCancelClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable("missedDose/{medicineId}/{doseIndex}") { backStackEntry ->

                val medicineId =
                    backStackEntry.arguments
                        ?.getString("medicineId")
                        ?.toIntOrNull()

                val doseIndex =
                    backStackEntry.arguments
                        ?.getString("doseIndex")
                        ?.toIntOrNull()

                val medicine = medicines.firstOrNull {
                    it.id == medicineId
                }

                if (medicine != null && doseIndex != null) {
                    val originalTime = medicine.reminderTimes
                        .getOrNull(doseIndex)
                        ?.time
                        ?: "-"

                    MissedDoseScreen(
                        medicineName = medicine.name,
                        dosage =
                            "${medicine.dosageAmount} ${medicine.dosageType}",
                        scheduledTime = originalTime,

                        onBackClick = {
                            navController.popBackStack()
                        },

                        onRescheduleClick = {
                            navController.navigate(
                                "rescheduleMedication/" +
                                        "${medicine.id}/$doseIndex"
                            )
                        },

                        onSkipClick = {
                            medicineVm.markDoseAsMissed(
                                medicineId = medicine.id,
                                doseIndex = doseIndex,
                                reminderTime = originalTime
                            )

                            navController.navigate("home") {
                                popUpTo("home") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable("medicationReminder/{medicineId}/{doseIndex}") { backStackEntry ->

                val medicineId =
                    backStackEntry.arguments
                        ?.getString("medicineId")
                        ?.toIntOrNull()

                val doseIndex =
                    backStackEntry.arguments
                        ?.getString("doseIndex")
                        ?.toIntOrNull()

                val medicine = medicines.firstOrNull {
                    it.id == medicineId
                }

                if (medicine != null && doseIndex != null) {
                    val originalTime = medicine.reminderTimes
                        .getOrNull(doseIndex)
                        ?.time
                        ?: "-"

                    MedicationReminderScreen(
                        medicineName = medicine.name,
                        dosage =
                            "${medicine.dosageAmount} ${medicine.dosageType}",
                        scheduledTime = originalTime,

                        onBackClick = {
                            navController.popBackStack()
                        },

                        onTakenClick = {
                            medicineVm.markDoseAsTaken(
                                medicineId = medicine.id,
                                doseIndex = doseIndex,
                                reminderTime = originalTime
                            )

                            navController.navigate(
                                "takenDose/${medicine.id}/$doseIndex"
                            ) {
                                launchSingleTop = true
                            }
                        },

                        onRescheduleClick = {
                            navController.navigate(
                                "rescheduleMedication/" +
                                        "${medicine.id}/$doseIndex"
                            )
                        }
                    )
                }
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
                        navigateBottomBar(
                            index = index,
                            navController = navController
                        )
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
                val monthlyStatisticsVm: MonthlyStatisticsViewModel =
                    viewModel()

                LaunchedEffect(takenRecords, missedRecords) {
                    monthlyStatisticsVm.updateRecords(
                        taken = takenRecords,
                        missed = missedRecords
                    )
                }

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
                    },
                    viewModel = monthlyStatisticsVm
                )
            }

            composable("medicationPerformance") {

                val medicationPerformanceVm:
                        MedicationPerformanceViewModel =
                    viewModel()

                LaunchedEffect(
                    takenRecords,
                    missedRecords,
                    medicines
                ) {
                    medicationPerformanceVm.updateRecords(
                        taken = takenRecords,
                        missed = missedRecords,
                        medicineList = medicines
                    )
                }

                MedicationPerformanceScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    viewModel = medicationPerformanceVm
                )
            }

            composable("missedMedication") {

                val missedMedicationVm: MissedMedicationViewModel =
                    viewModel()

                LaunchedEffect(missedRecords, medicines) {
                    missedMedicationVm.updateRecords(
                        missed = missedRecords,
                        medicineList = medicines
                    )
                }

                MissedMedicationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = missedMedicationVm
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
                        navigateBottomBar(
                            index = index,
                            navController = navController
                        )
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
            )
            { backStackEntry ->

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
                                    message = "Refill successful. Remaining quantity: $remainingText",
                                    duration = SnackbarDuration.Short
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
            )
            { backStackEntry ->

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
                                            "This medicine was already taken within the last minute.",
                                        duration = SnackbarDuration.Short
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
                                    message = "$medicineName was deleted successfully.",
                                    duration = SnackbarDuration.Short
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
            )
            { backStackEntry ->

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
            )
            { backStackEntry ->
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

@Composable
fun MissedDoseScreen(
    medicineName: String,
    dosage: String,
    scheduledTime: String,
    onBackClick: () -> Boolean,
    onRescheduleClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    TODO("Not yet implemented")
}

private fun navigateBottomBar(
    index: Int,
    navController: androidx.navigation.NavHostController
) {
    val route =
        when (index) {
            0 -> "home"
            1 -> "medicine"
            2 -> "history"
            3 -> "profile"
            else -> return
        }

    if (
        navController.currentDestination?.route != route
    ) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}