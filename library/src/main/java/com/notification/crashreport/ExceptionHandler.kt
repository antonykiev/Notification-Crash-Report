package com.notification.crashreport

import java.io.PrintWriter
import java.io.StringWriter

class ExceptionHandler(
    private val onException: (String) -> Unit,
) {
    fun init() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val exceptionText = sw.toString()
            onException(exceptionText)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}