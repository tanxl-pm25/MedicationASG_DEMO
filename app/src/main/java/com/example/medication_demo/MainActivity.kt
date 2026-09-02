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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.navigation.MedicationApp
import com.example.medication_demo.repository.AppointmentRepository
import com.example.medication_demo.ui.theme.Medication_DemoTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {

    // 记录这次 deep link 是否为密码重设连结
    private var pendingDeepLinkType by mutableStateOf<String?>(null)

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

        handleIncomingIntent(intent)

        refillMedicineId.value =
            intent.getIntExtra(
                "refillMedicineId",
                -1
            ).takeIf {
                it != -1
            }

        AppointmentRepository.initialize(applicationContext)

        setContent {
            Medication_DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MedicationApp(
                        pendingDeepLinkType = pendingDeepLinkType,
                        onDeepLinkConsumed = {
                            pendingDeepLinkType = null
                        },
                        notificationMedicineId = refillMedicineId.value,
                        onNotificationHandled = {
                            refillMedicineId.value = null
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
}