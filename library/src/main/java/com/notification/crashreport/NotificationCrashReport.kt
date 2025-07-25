package com.notification.crashreport

import android.app.Application

class NotificationCrashReport(
    val app: Application,
    var isDebug: Boolean = false,
    var chooserMessage: String = "Share Crash Report",
    var notification: NotificationData = NotificationData(),
) {

    fun build(): NotificationCrashReport {
        return NotificationCrashReport(
            app = app,
            isDebug = isDebug,
            chooserMessage = chooserMessage,
            notification = notification
        )
    }
}