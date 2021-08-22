package com.rasalexman.sresultpresentation.tools

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class OnSwipeTouchListener(context: Context) : OnTouchListener {
    private val gestureDetector: GestureDetector
    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    open fun onSwipeLeft() = Unit
    open fun onSwipeRight() = Unit
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY) && abs(distanceX) > Companion.SWIPE_DISTANCE_THRESHOLD && abs(
                    velocityX
                ) > Companion.SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
                return true
            }
            return false
        }
    }

    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}