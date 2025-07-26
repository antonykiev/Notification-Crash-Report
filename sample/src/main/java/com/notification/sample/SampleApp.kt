package com.notification.sample

import android.app.Application
import com.notification.crashreport.notification
import com.notification.crashreport.notificationCrashReport

class SampleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        notificationCrashReport {
            isDebug = true
            notification {
                title = "Sample Notification"
                message = "This is a sample notification message."
                icon = R.drawable.ic_danger_icon
            }
        }
    }
}