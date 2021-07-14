package config

object Libs {
    object Core {
        //const val appcompat = "androidx.appcompat:appcompat:$appCompatX"
        const val coreKtx = "androidx.core:core-ktx:${Versions.appCoreX}"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    }

    object Lifecycle {
        // kotlin view model
        const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        // view model saved state handler
        const val savedStateViewModel = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
        // kotlin live data extensions
        const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
        // alternately - if using Java8, use the following instead of lifecycle-compiler
        // const val common = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    }

    object Common {
        //---- COROUTINES MANAGER
        const val coroutinesmanager = "com.github.Rasalexman:coroutinesmanager:${Versions.coroutinesManager}"

        //--- Data Binding
        const val easyRecyclerBinding = "com.github.Rasalexman.easy-recycler-binding:easyrecyclerbinding:${Versions.easyBinding}"

        //----- DI
        const val kodi = "com.github.Rasalexman.KODI:kodi:${Versions.kodi}"
        const val kodigen = "com.github.Rasalexman.KODI:kodigen:${Versions.kodi}"

        //---- LOGGING TIMER =)
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

        //--- LEAK DETECTOR
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
    }

    object Tests {
        const val junit = "junit:junit:${Versions.junit}"
        const val runner = "com.android.support.test:runner:${Versions.runner}"
        const val espresso = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    }

}