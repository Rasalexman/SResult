package com.rasalexman.sresult.data.dto

import kotlin.native.concurrent.ThreadLocal

/**
 * States
 */
@Suppress("UNCHECKED_CAST")
abstract class SResult<out T : Any> : ISResult {
    open val data: T? = null
    open var isNeedHandle: Boolean = false
    open var isHandled: Boolean = false

    override fun isSuccess(): Boolean {
        return this is Success
    }

    override fun isError(): Boolean {
        return this is AbstractFailure.Error
    }

    override fun isLoading(): Boolean {
        return this is Loading
    }

    override fun isEmpty(): Boolean {
        return this is Empty
    }

    override fun flatMap(mapper: (ISResult) -> ISResult): ISResult {
        return mapper.invoke(this)
    }

    override fun <R> flatMapOnSuccess(onSuccess: (R) -> ISResult): ISResult {
        if(this is Success) {
            (data as? R)?.let {
                return onSuccess.invoke(it)
            }
        }
        return this
    }

    override fun flatMapOnEmpty(onEmpty: () -> ISResult): ISResult {
        if(this is Empty) {
            return onEmpty.invoke()
        }
        return this
    }

    override fun flatMapOnError(onError: (AbstractFailure.Error) -> ISResult): ISResult {
        if(this is AbstractFailure.Error) {
            return onError.invoke(this)
        }
        return this
    }

    override fun <R> applyOnSuccess(onSuccess: (R) -> Unit): ISResult {
        if(this is Success) {
            (data as? R)?.let(onSuccess)
        }
        return this
    }

    override fun applyOnEmpty(onEmpty: () -> Unit): ISResult {
        if(this is Empty) {
            onEmpty.invoke()
        }
        return  this
    }

    override fun applyOnError(onError: (AbstractFailure.Error) -> Unit): ISResult {
        if(this is AbstractFailure.Error) {
            onError.invoke(this)
        }
        return this
    }

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
    open class EmptySuccess : Success<Any>(true)

    /**
     * Any Success result
     */
    object AnySuccess : EmptySuccess()

    /**
     * Loading result state
     */
    class Loading(
        override var isNeedHandle: Boolean = true
    ) : NothingResult()

    /**
     * Progress event type
     */
    class Progress(
        val progress: Int = 0,
        val message: Any? = null,
        val isPlus: Boolean = false,
        override var isNeedHandle: Boolean = true
    ) : NothingResult()

    /**
     * State to show toast on ui
     */
    class Toast(
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
    @ThreadLocal
    object EmptyUnhandled : NothingResult() {
        override var isHandled: Boolean = false
        override var isNeedHandle: Boolean = false
    }

    //---- Error States
    abstract class AbstractFailure : NothingResult() {
        open val message: Any? = null
        open val exception: Throwable? = null
        open val code: Int = 0
        open val errors: List<Throwable>? = null
        open val interval: Int = 1
        override var isNeedHandle = true

        /**
         *
         */
        abstract class Failure : AbstractFailure()

        class Error(
            override val message: Any? = null,
            override val code: Int = 0,
            override val exception: Throwable? = null,
            override val errors: List<Throwable> = emptyList()
        ) : Failure()

        class Alert(
            override val message: Any? = null,
            override val code: Int = 0,
            override val exception: Throwable? = null,
            val dialogTitle: Any? = null,
            val okHandler: (() -> Unit)? = null,
            val cancelHandler: (() -> Unit)? = null,
            val okTitle: Any? = null,
            val cancelTitle: Any? = null
        ) : Failure()
    }

    //---- Navigation States
    abstract class NavigateResult : NothingResult() {
        override var isNeedHandle = true
        open val navDirection: Any? = null
        open val navigateResourceId: Int? = null
        open var args: Map<String, Any?>? = null

        abstract class BaseNavigationResult : NavigateResult()

        //
        object EmptyNavigation : NavigateResult()

        class NavigateTo(
            override val navDirection: Any
        ) : BaseNavigationResult()

        class NavigateBy(
            override val navigateResourceId: Int
        ) : BaseNavigationResult()

        class NavigatePopTo(
            override val navigateResourceId: Int? = null,
            val isInclusive: Boolean = false
        ) : BaseNavigationResult()

        class NavigateNext : BaseNavigationResult()
        class NavigateBack : BaseNavigationResult()
        class NavigatePop : BaseNavigationResult()
    }
}







