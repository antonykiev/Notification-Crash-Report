package com.notification.crashreport

//noinspection SuspiciousImport
import android.R
import androidx.annotation.DrawableRes

class NotificationData(
    var title: String = "Application Crash",
    var message: String = "An error occurred:",
    @DrawableRes val icon: Int = R.drawable.ic_dialog_alert,
    var notificationId: Int = 1001,
    var notificationChannelDescription: String = "Channel for exception notifications",
    var notificationChannelName: String = "Exception Channel",
    var notificationChannelId: String = "exception_channel",
) {
    fun build(): NotificationData {
        return NotificationData(
            title = title,
            message = message,
            notificationId = notificationId,
            notificationChannelId = notificationChannelId,
            icon = icon
        )
    }
}