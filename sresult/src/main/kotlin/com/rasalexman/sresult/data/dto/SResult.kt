package com.rasalexman.sresult.data.dto

import android.os.Bundle
import androidx.navigation.NavDirections
import com.rasalexman.sresult.common.typealiases.UnitHandler

/**
 * States
 */
sealed class SResult<out T : Any> {

    companion object {
        private const val SUCCESS_DEFAULT_VALUE = true
    }

    open val data: T? = null
    open var isNeedHandle: Boolean = false
    open var isHandled: Boolean = false

    /**
     *
     */
    abstract class AbstractResult<out T : Any> : SResult<T>()

    /**
     * Base Empty Result state
     */
    abstract class NothingResult : AbstractResult<Nothing>()

    /**
     * Success state with data
     */
    open class Success<out T : Any>(
        override val data: T
    ) : AbstractResult<T>()

    /**
     * Any Success result
     */
    open class EmptySuccess : Success<Any>(SUCCESS_DEFAULT_VALUE)

    /**
     * Any Success result
     */
    object AnySuccess : EmptySuccess()

    /**
     * Loading result state
     */
    data class Loading(
        override var isNeedHandle: Boolean = true
    ) : NothingResult()

    /**
     * Progress event type
     */
    data class Progress(
        val progress: Int = 0,
        val message: Any? = null,
        val isPlus: Boolean = false,
        override var isNeedHandle: Boolean = true
    ) : NothingResult()

    /**
     * State to show toast on ui
     */
    data class Toast(
        val message: Any?,
        val interval: Int = 0,
        override var isNeedHandle: Boolean = true
    ) : NothingResult()

    /**
     * Any empty State with no result
     */
    object Empty : NothingResult()

    /**
     * Any empty State with no result
     */
    object EmptyUnhandled : NothingResult() {
        override var isHandled: Boolean = false
        override var isNeedHandle: Boolean = false
    }

    //---- Navigation States
    sealed class NavigateResult : NothingResult() {
        override var isNeedHandle = true
        open val navDirection: NavDirections? = null
        open val navigateResourceId: Int? = null
        open var args: Bundle? = null

        abstract class BaseNavigationResult : NavigateResult()

        data class NavigateTo(
            override val navDirection: NavDirections
        ) : BaseNavigationResult()

        data class NavigateBy(
            override val navigateResourceId: Int
        ) : BaseNavigationResult()

        data class NavigatePopTo(
            override val navigateResourceId: Int? = null,
            val isInclusive: Boolean = false
        ) : BaseNavigationResult()

        class NavigateNext : BaseNavigationResult()
        class NavigateBack : BaseNavigationResult()
        class NavigatePop : BaseNavigationResult()
    }

    //---- Error States
    sealed class AbstractFailure : NothingResult() {
        open val message: Any? = null
        open val exception: Throwable? = null
        open val code: Int = 0
        open val interval: Int = 1
        override var isNeedHandle = true

        /**
         *
         */
        abstract class Failure : AbstractFailure()

        data class Error(
            override val message: Any? = null,
            override val code: Int = 0,
            override val exception: Throwable? = null
        ) : Failure()

        data class Alert(
            override val message: Any? = null,
            override val code: Int = 0,
            override val exception: Throwable? = null,
            val dialogTitle: Any? = null,
            val okHandler: UnitHandler? = null,
            val cancelHandler: UnitHandler? = null,
            val okTitle: Any? = null,
            val cancelTitle: Any? = null
        ) : Failure()
    }
}