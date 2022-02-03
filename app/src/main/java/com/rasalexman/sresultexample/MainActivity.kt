package com.rasalexman.sresultexample

import com.rasalexman.sresultpresentation.activity.BaseNavigationCompatActivity

class MainActivity : BaseNavigationCompatActivity(R.layout.activity_main) {
    override val navHostContainerId: Int
        get() = R.id.mainHostFragment
}
