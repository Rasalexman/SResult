//------ APP VERSION
extra["appVersion"] = "1.3.43"
extra["mainGroupName"] = "com.rasalexman.sresult"
extra["supportGroupName"] = "com.rasalexman.sresultpresentation"

//------ CONFIG DATA
extra["minSdkVersion"] = 18
extra["buildSdkVersion"] = 31
extra["toolsVersion"] = "31.0.0"
extra["apiVersion"] = "1.6"
extra["jvmVersion"] = "11"
extra["agpVersion"] = "7.1.2"
extra["kotlinVersion"] = "1.6.10"
extra["jitpackPath"] = "https://jitpack.io"
extra["codePath"] = "src/main/kotlin"
extra["resPath"] = "src/main/res"

//------- LIBS VERSIONS
val navigation = "2.5.0-alpha03"
val leakcanary = "2.8.1"
val coroutines = "1.6.0"
val core: String = "1.7.0"
val constraintLayout = "2.1.3"
val material = "1.6.0-alpha03"
val viewPager2 = "1.1.0-beta01"
val recyclerView = "1.3.0-alpha01"
val lifecycle = "2.5.0-alpha04"
val fragment: String = "1.5.0-alpha03"
val paging: String = "3.1.0"
val timber = "5.0.1"
val kodi = "1.6.2"
val coroutinesManager = "1.4.0"
val easyBinding = "1.2.6"
extra["navigation"] = navigation

//------- Libs path
extra["leakCanary"] = "com.squareup.leakcanary:leakcanary-android:$leakcanary"
extra["coroutines"] = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
extra["coreKtx"] = "androidx.core:core-ktx:$core"
extra["timber"] = "com.jakewharton.timber:timber:$timber"
extra["kodi"] = "com.github.Rasalexman.KODI:kodi:$kodi"
extra["coroutinesmanager"] = "com.github.Rasalexman:coroutinesmanager:$coroutinesManager"
extra["easyrecyclerbinding"] = "com.github.Rasalexman.easy-recycler-binding:easyrecyclerbinding:$easyBinding"
extra["constraintlayout"] = "androidx.constraintlayout:constraintlayout:$constraintLayout"
extra["navigationUI"] = "androidx.navigation:navigation-ui-ktx:$navigation"
extra["navigationFragment"] = "androidx.navigation:navigation-fragment-ktx:$navigation"
extra["material"] = "com.google.android.material:material:$material"
extra["viewpager2"] = "androidx.viewpager2:viewpager2:$viewPager2"
extra["recyclerview"] = "androidx.recyclerview:recyclerview:$recyclerView"
extra["fragmentKtx"] = "androidx.fragment:fragment-ktx:$fragment"
extra["paging"] = "androidx.paging:paging-runtime:$paging"
extra["viewmodelKtx"] = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
extra["livedataKtx"] = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"
