package com.rasalexman.sresultexample

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rasalexman.kodi.core.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTimber()

        kodi {
            bind<Context>() with single { this@MainActivity.applicationContext }
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
