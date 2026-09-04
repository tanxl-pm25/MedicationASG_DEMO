package com.example.medication_demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.navigation.MedicationApp
import com.example.medication_demo.reminder.MedicationNotification
import com.example.medication_demo.repository.AppointmentRepository
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import com.example.medication_demo.utils.ThemePreference
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    private var pendingDeepLinkType by mutableStateOf<String?>(null)

    private val refillMedicineId = mutableStateOf<Int?>(null)
    private val navigateToHome = mutableStateOf(false)

    private var medicationNotificationAction by mutableStateOf<String?>(null)
    private var medicationNotificationMedicineId by mutableStateOf<Int?>(null)
    private var medicationNotificationDoseIndex by mutableStateOf<Int?>(null)
    private var medicationNotificationOriginalTime by mutableStateOf<String?>(null)

    private var isDarkMode by mutableStateOf(false)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // No extra action is needed after Allow / Don't allow.
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        handleIncomingIntent(intent)
        readMedicationNotificationIntent(intent)

        navigateToHome.value =
            intent.getBooleanExtra(
                "navigateToHome",
                false
            )

        refillMedicineId.value =
            intent.getIntExtra(
                "refillMedicineId",
                -1
            ).takeIf {
                it != -1
            }

        isDarkMode = ThemePreference.isDarkMode(this)

        AppointmentRepository.initialize(
            applicationContext
        )

        setContent {
            Medication_DemoTheme(
                darkTheme = isDarkMode
            ) {
                val view = LocalView.current

                if (!view.isInEditMode) {
                    SideEffect {
                        WindowCompat
                            .getInsetsController(
                                window,
                                view
                            )
                            .isAppearanceLightStatusBars =
                            !isDarkMode
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MedicationApp(
                        pendingDeepLinkType = pendingDeepLinkType,
                        onDeepLinkConsumed = {
                            pendingDeepLinkType = null
                        },
                        notificationMedicineId =
                            refillMedicineId.value,
                        onNotificationHandled = {
                            refillMedicineId.value = null
                        },
                        navigateToHomeFromNotification =
                            navigateToHome.value,
                        onHomeNavigationHandled = {
                            navigateToHome.value = false
                        },
                        medicationNotificationAction =
                            medicationNotificationAction,
                        medicationNotificationMedicineId =
                            medicationNotificationMedicineId,
                        medicationNotificationDoseIndex =
                            medicationNotificationDoseIndex,
                        medicationNotificationOriginalTime =
                            medicationNotificationOriginalTime,
                        onMedicationNotificationHandled = {
                            medicationNotificationAction = null
                            medicationNotificationMedicineId = null
                            medicationNotificationDoseIndex = null
                            medicationNotificationOriginalTime = null
                        },
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { newValue ->
                            isDarkMode = newValue
                            ThemePreference.setDarkMode(
                                this@MainActivity,
                                newValue
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)

        setIntent(intent)

        handleIncomingIntent(intent)
        readMedicationNotificationIntent(intent)

        if (
            intent.getBooleanExtra(
                "navigateToHome",
                false
            )
        ) {
            navigateToHome.value = true
        }

        val medicineId =
            intent.getIntExtra(
                "refillMedicineId",
                -1
            )

        if (medicineId != -1) {
            refillMedicineId.value = medicineId
        }
    }

    private fun handleIncomingIntent(
        intent: Intent
    ) {
        val uriString = intent.data?.toString()

        if (
            uriString != null &&
            uriString.contains("type=recovery")
        ) {
            pendingDeepLinkType = "recovery"
        }

        SupabaseClientProvider.client
            .handleDeeplinks(intent)
    }

    private fun readMedicationNotificationIntent(
        intent: Intent
    ) {
        val action =
            intent.getStringExtra(
                MedicationNotification.EXTRA_ACTION
            ) ?: return

        val medicineId =
            intent.getIntExtra(
                MedicationNotification.EXTRA_MEDICINE_ID,
                -1
            )

        val doseIndex =
            intent.getIntExtra(
                MedicationNotification.EXTRA_DOSE_INDEX,
                -1
            )

        val originalTime =
            intent.getStringExtra(
                MedicationNotification.EXTRA_ORIGINAL_TIME
            ) ?: return

        if (medicineId == -1 || doseIndex == -1) {
            return
        }

        medicationNotificationAction = action
        medicationNotificationMedicineId = medicineId
        medicationNotificationDoseIndex = doseIndex
        medicationNotificationOriginalTime = originalTime
    }
}