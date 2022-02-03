package com.rasalexman.sresultexample

import android.app.Application
import android.content.Context
import com.rasalexman.kodi.core.bind
import com.rasalexman.kodi.core.kodi
import com.rasalexman.kodi.core.single
import com.rasalexman.kodi.core.with
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        kodi {
            bind<Context>() with single { this@MainApplication.applicationContext }
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    println("message:$message Throwable:$t")
                }
            })
        }
    }
}