package com.rasalexman.sresultpresentation.tools

import android.os.SystemClock
import android.view.View
import java.util.*
import kotlin.math.abs

/**
 * The one and only constructor
 *
 * @param minimumIntervalMillis The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
 */
abstract class DebouncedOnClickListener(private val minimumIntervalMillis: Long) : View.OnClickListener {

    /**
     * View callbacks timestamps holder
     */
    private val lastClickMap: MutableMap<View, Long> = WeakHashMap()

    /**
     * Implement this in your subclass instead of onClick
     *
     * @param v The view that was clicked
     */
    abstract fun onDebouncedClick(v: View?)
    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView]
        val currentTimestamp = SystemClock.uptimeMillis()
        lastClickMap[clickedView] = currentTimestamp
        if (previousClickTimestamp == null || abs(currentTimestamp - previousClickTimestamp) > minimumIntervalMillis) {
            onDebouncedClick(clickedView)
        }
    }
}