package com.rasalexman.sresultpresentation.base

import android.os.Bundle

interface INavigateHandler : IProgressHandler, IControlHandler {
    fun navigateTo(direction: Any)
    fun navigateBy(navResId: Int)
    fun navigatePopTo(
        navResId: Int? = null,
        isInclusive: Boolean = false,
        backArgs: Bundle?
    )
    fun navigatePop(backArgs: Bundle?)

    fun showNavigationError(e: Exception? = null, navResId: Int?)
}