package com.notification.crashreport

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun Application.notificationCrashReport(
    action: NotificationCrashReport.() -> Unit,
): NotificationCrashReport {
    val application = this

    val report: NotificationCrashReport = NotificationCrashReport(this)
        .apply(action)
        .build()
    val notification: NotificationData = report.notification

    if (!report.isDebug) return report

    ExceptionHandler(
        onException = { errorMessage ->
            if (checkForSelfNotificationPermission()) {
                showNotification(
                    application = application,
                    report = report,
                    notification = notification,
                    errorMessage = errorMessage
                )
            }
        }
    ).init()

    registerActivityLifecycleCallback()
    return report
}

fun NotificationCrashReport.notification(
    action: NotificationData.() -> Unit,
): NotificationData {
    return NotificationData()
        .apply(action)
        .build()
}


private fun Application.checkForSelfNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun Application.registerActivityLifecycleCallback() {
    registerActivityLifecycleCallbacks(
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                    }
                }
            }

            override fun onActivityDestroyed(p0: Activity) {}
            override fun onActivityPaused(p0: Activity) {}
            override fun onActivityResumed(p0: Activity) {}
            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
            override fun onActivityStarted(p0: Activity) {}
            override fun onActivityStopped(p0: Activity) {}
        }
    )
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
private fun showNotification(
    application: Application,
    report: NotificationCrashReport,
    notification: NotificationData,
    errorMessage: String,
) {
    val notificationManager = NotificationManagerCompat.from(application)

    createNotificationChannel(
        application = application,
        channelId = notification.notificationChannelId,
        channelName = notification.notificationChannelDescription ,
        channelDescription = notification.notificationChannelDescription
    )

    val pendingIntent = pendingIntent(
        application = application,
        chooserMessage = report.chooserMessage,
        errorMessage = errorMessage
    )

    notificationManager.notify(
        System.currentTimeMillis().toInt(),
        NotificationCompat.Builder(application, notification.notificationChannelId)
            .setSmallIcon(notification.icon)
            .setLargeIcon(BitmapFactory.decodeResource(application.resources, notification.icon))
            .setContentTitle(notification.title)
            .setContentText(notification.notificationChannelDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    )
}

fun createNotificationChannel(
    application: Application,
    channelId: String,
    channelName: String,
    channelDescription: String,
) {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
        // Optional: Customize channel settings
        enableLights(true)
        lightColor = android.graphics.Color.RED
        enableVibration(true)
    }

    val notificationManager = application.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

private fun pendingIntent(
    application: Application,
    chooserMessage: String,
    errorMessage: String,
): PendingIntent {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, errorMessage)
        putExtra(Intent.EXTRA_SUBJECT, "Crash Report")
    }
    val chooserIntent = Intent.createChooser(shareIntent, chooserMessage)

    return PendingIntent.getActivity(
        /* context = */ application,
        /* requestCode = */ 0,
        /* intent = */ chooserIntent,
        /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}