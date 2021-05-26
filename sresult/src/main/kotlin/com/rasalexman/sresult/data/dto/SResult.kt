package com.rasalexman.sresult.data.dto

import androidx.navigation.NavDirections

/**
 * States
 */
sealed class SResult<out T : Any> : ISResult<T> {

    override val data: T? = null
    override var isNeedHandle: Boolean = false
    override var isHandled: Boolean = false

    /**
     * Success state with data
     */
    open class Success<out T : Any>(
        override val data: T
    ) : SResult<T>()

    /**
     * Any Success result
     */
    object AnySuccess : Success<Any>(true)

    /**
     * Loading result state
     */
    data class Loading(override var isNeedHandle: Boolean = true) : SResult<Nothing>()

    /**
     * Progress event type
     */
    data class Progress(
        override var isNeedHandle: Boolean = true,
        override val data: Int = 0
    ) : SResult<Int>()

    /**
     * State to show toast on ui
     */
    data class Toast(val message: Any?) : SResult<Any>() {
        override var isNeedHandle: Boolean = true
    }

    /**
     * Any empty State with no result
     */
    object Empty : SResult<Nothing>()

    //---- Navigation States
    sealed class NavigateResult : SResult<Any>() {
        override var isNeedHandle = true

        data class NavigateTo(
            val navDirection: NavDirections
        ) : NavigateResult()

        data class NavigateBy(
            val navigateResourceId: Int
        ) : NavigateResult()

        data class NavigatePopTo(
            val navigateResourceId: Int? = null,
            val isInclusive: Boolean = false
        ) : NavigateResult()

        class NavigateNext : NavigateResult()
        class NavigateBack : NavigateResult()
        class Stay: NavigateResult()
        class CloseBottomSheet: NavigateResult()
    }

    //---- Error States
    sealed class ErrorResult : SResult<Nothing>() {
        open val message: Any? = null
        open val exception: Throwable? = null
        override var isNeedHandle = true

        data class Error(
            override val message: Any? = null,
            val code: Int = 0,
            override val exception: Throwable? = null
        ) : ErrorResult()

        data class Alert(
            override val message: Any? = null,
            val dialogTitle: Any? = null,
            override val exception: Throwable? = null,
            val okHandler: (() -> Unit)? = null,
            val okTitle: Int? = null
        ) : ErrorResult()
    }
}