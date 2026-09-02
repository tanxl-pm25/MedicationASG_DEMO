package com.example.medication_demo

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import com.example.medication_demo.data.SupabaseClientProvider
import com.example.medication_demo.ui.theme.Medication_DemoTheme

import com.example.medication_demo.navigation.MedicationApp
import io.github.jan.supabase.auth.handleDeeplinks


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.compose.ui.Modifier


class MainActivity : ComponentActivity() {
    // 记录这次deep link是不是"密码重设"类型的连结
    private var pendingDeepLinkType by mutableStateOf<String?>(null)
    private val refillMedicineId = mutableStateOf<Int?>(null)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        handleIncomingIntent(intent)

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

        setContent {
            Medication_DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    MedicationApp(
                        pendingDeepLinkType = pendingDeepLinkType,
                        onDeepLinkConsumed = { pendingDeepLinkType = null },
                        notificationMedicineId = refillMedicineId.value,
                        onNotificationHandled = {
                            refillMedicineId.value = null
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

        handleIncomingIntent(intent)
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
    private fun handleIncomingIntent(intent: Intent) {
        // Supabase的密码重设连结,网址里会带"type=recovery"这个字样
        val uriString = intent.data?.toString()
        if (uriString != null && uriString.contains("type=recovery")) {
            pendingDeepLinkType = "recovery"
        }

        // 处理Google登入/密码重设完成、浏览器跳回app时带的deep link
        SupabaseClientProvider.client.handleDeeplinks(intent)
    }

}