package com.rasalexman.sresultexample

import android.os.Bundle
import android.os.PersistableBundle
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresultpresentation.activity.BaseNavigationCompatActivity

class MainActivity : BaseNavigationCompatActivity(R.layout.activity_main) {
    override val navHostContainerId: Int
        get() = R.id.mainHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "%hello".apply {
            loggE { "world" }
        }
    }
}
