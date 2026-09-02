package com.example.medication_demo

import com.example.medication_demo.viewmodel.WaterIntakeViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.medication_demo.navigation.MedicationApp
import com.example.medication_demo.repository.AppointmentRepository
import com.example.medication_demo.ui.theme.Medication_DemoTheme

class MainActivity : ComponentActivity() {

    private val refillMedicineId =
        mutableStateOf<Int?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // 用户允许或拒绝后，不需要额外处理
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        enableEdgeToEdge()

        // If app is opened by tapping the notification
        refillMedicineId.value =
            intent
                ?.getIntExtra(
                    "refillMedicineId",
                    -1
                )
                ?.takeIf {
                    it != -1
                }


        AppointmentRepository.initialize(applicationContext)
        WaterIntakeViewModel().initialize(applicationContext)

        // If app is opened by tapping the notification
        refillMedicineId.value =
            intent?.getIntExtra(
                "refillMedicineId",
                -1
            )?.takeIf {
                it != -1
            }

        setContent {

            Medication_DemoTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    MedicationApp(
                        notificationMedicineId =
                            refillMedicineId.value,

                        onNotificationHandled = {
                            refillMedicineId.value =
                                null
                        }
                    )
                }
            }
        }
    }

    // Called when MainActivity already exists
    // and user taps the notification
    override fun onNewIntent(
        intent: Intent
    ) {
        super.onNewIntent(intent)

        setIntent(intent)

        val medicineId =
            intent.getIntExtra(
                "refillMedicineId",
                -1
            )

        if (medicineId != -1) {
            refillMedicineId.value =
                medicineId
        }
    }
}