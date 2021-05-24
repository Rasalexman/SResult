package com.rasalexman.sresult.common.extensions

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

fun Context.restartApp() {
    val packageManager = packageManager
    packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
        val componentName = intent.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        this.startActivity(mainIntent)
        exitProcess(0)
    }
}