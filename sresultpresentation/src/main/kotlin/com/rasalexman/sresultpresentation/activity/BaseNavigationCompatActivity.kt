package com.rasalexman.sresultpresentation.activity

import android.graphics.Rect
import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresultpresentation.extensions.hideKeyboard
import com.rasalexman.sresultpresentation.fragments.INavigationHandler

abstract class BaseNavigationCompatActivity : AppCompatActivity {

    constructor() : super()
    constructor(@LayoutRes layoutResId: Int) : super(layoutResId)

    abstract val navHostContainerId: Int

    protected open val mainHostFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(navHostContainerId)

    protected open val currentFragment: Fragment?
        get() = mainHostFragment?.childFragmentManager?.primaryNavigationFragment

    protected open val currentNavHandler: INavigationHandler?
        get() = currentFragment as? INavigationHandler

    override fun onSupportNavigateUp(): Boolean {
        val isSupportNotNavUp = currentNavHandler?.onSupportNavigateUp() == false
        val isMainNavUp = mainHostFragment?.findNavController()?.navigateUp() == true
        return (isSupportNotNavUp && isMainNavUp)
    }

    override fun onBackPressed() {
        hideKeyboard()
        val isBackPressedHandled = currentNavHandler?.onBackPressed()
        if (isBackPressedHandled == false) {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        processTouchDispatch(event)
        return super.dispatchTouchEvent(event)
    }

    protected open fun processTouchDispatch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard()
                }
            }
        }
    }
}