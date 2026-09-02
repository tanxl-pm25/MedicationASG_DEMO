package com.example.medication_demo.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medication_demo.R

object NotificationHelper {

    private const val CHANNEL_ID = "auth_notifications"
    private const val CHANNEL_NAME = "Account Notifications"
    private const val VERIFICATION_NOTIFICATION_ID = 1001

    // Android 8.0(API 26)以上一定要先建channel,不然通知发不出去
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about your account, such as email verification."
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // 点了通知之后要打开的Intent:优先打开Gmail app,没装的话跳系统预设email app
    private fun buildOpenEmailIntent(context: Context): Intent {
        val gmailIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
        if (gmailIntent != null) {
            return gmailIntent
        }

        // 没装Gmail的话,跳出系统"选一个email app打开"的清单
        return Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
    }

    // 显示"验证码已发送"的通知(不含真正的验证码),点了会跳去Gmail
    @SuppressLint("MissingPermission")
    fun showVerificationSentNotification(context: Context, email: String) {
        val emailIntent = buildOpenEmailIntent(context)

        val pendingIntent = PendingIntent.getActivity(
            context,
            VERIFICATION_NOTIFICATION_ID,
            emailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.heart_logo)
            .setContentTitle("Verification code sent")
            .setContentText("We've sent a 6-digit code to $email. Tap to check your inbox.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Android 13(API 33)以上要先确认使用者有给通知权限,不然会crash
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        ) {
            NotificationManagerCompat.from(context).notify(
                VERIFICATION_NOTIFICATION_ID,
                notification
            )
        }
    }
}