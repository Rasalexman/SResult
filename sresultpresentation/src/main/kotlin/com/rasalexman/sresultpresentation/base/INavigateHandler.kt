package com.rasalexman.sresultpresentation.base

import androidx.navigation.NavDirections

interface INavigateHandler : IProgressHandler, IControlHandler {
    fun navigateTo(direction: NavDirections)
    fun navigateBy(navResId: Int)
    fun navigatePopTo(
        navResId: Int? = null,
        isInclusive: Boolean = false
    )

    fun showNavigationError(e: Exception? = null, navResId: Int?)
}