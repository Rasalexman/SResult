package com.rasalexman.sresultpresentation.base

import android.os.Bundle
import androidx.navigation.NavDirections

interface INavigateHandler : IProgressHandler, IControlHandler {
    fun navigateTo(direction: NavDirections)
    fun navigateBy(navResId: Int)
    fun navigatePopTo(
        navResId: Int? = null,
        isInclusive: Boolean = false,
        backArgs: Bundle?
    )
    fun navigatePop(backArgs: Bundle?)

    fun showNavigationError(e: Exception? = null, navResId: Int?)
}