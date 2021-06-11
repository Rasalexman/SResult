package com.rasalexman.sresult.common.extensions

import androidx.navigation.NavDirections
import com.rasalexman.sresult.common.typealiases.*
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresult.data.exception.ISException
import com.rasalexman.sresult.models.IConvertable
import com.rasalexman.sresult.models.IConvertableSuspend
import com.rasalexman.sresult.models.convert

// /------ ViewResult extensions
inline fun <reified T : Any> Any.successResult(data: T): SResult<T> = SResult.Success<T>(data)

fun Any?.loadingResult(isNeedHandle: Boolean = true) = SResult.Loading(isNeedHandle)
fun Any?.emptyResult(isNeedHandle: Boolean = true) = if(isNeedHandle) {
    SResult.Empty
} else {
    SResult.EmptyUnhandled
}

fun Any.anySuccess() = SResult.AnySuccess

fun <T : Any> Any.progressResult(
    progress: Int,
    message: Any? = null,
    isPlus: Boolean = false,
    isNeedHandle: Boolean = true
): SResult<T> =
    SResult.Progress(
        progress = progress, message = message, isPlus = isPlus, isNeedHandle = isNeedHandle
    )

fun Int.toProgress(message: Any? = null, isNeedHandle: Boolean = true) =
    SResult.Progress(progress = this, message = message, isNeedHandle = isNeedHandle)

fun Any.navigateToResult(
    to: NavDirections
) = SResult.NavigateResult.NavigateTo(to)

fun Any.navigateBy(
    navigateResourceId: Int
) = SResult.NavigateResult.NavigateBy(navigateResourceId)

fun Any.navigatePopTo(
    navigateResourceId: Int? = null,
    isInclusive: Boolean = false
) = SResult.NavigateResult.NavigatePopTo(navigateResourceId, isInclusive)

fun Any.navigateBackResult() =
    SResult.NavigateResult.NavigateBack()

fun Any.navigateNextResult() =
    SResult.NavigateResult.NavigateNext()

fun Any.errorResult(
    message: String = "",
    code: Int = -1,
    exception: Throwable? = null
) = SResult.AbstractFailure.Error(message, code, exception)

fun Any.alertResult(
    dialogMessage: Any? = null,
    exception: Throwable? = null,
    dialogTitle: Any? = null,
    okHandler: UnitHandler? = null,
    cancelHandler: UnitHandler? = null,
    okTitle: Any? = null,
    cancelTitle: Any? = null
) = SResult.AbstractFailure.Alert(
    message = dialogMessage,
    exception = exception,
    dialogTitle = dialogTitle,
    okHandler = okHandler,
    cancelHandler = cancelHandler,
    okTitle = okTitle,
    cancelTitle = cancelTitle
)

fun Any.toastResult(message: Any?) = SResult.Toast(message)

// /-------- toState Convertables
inline fun <reified T : Any> T?.toSuccessResult(orDefault: SResult<T> = emptyResult()): SResult<T> =
    this?.let {
        successResult(it)
    } ?: orDefault

inline fun <reified T : Any> List<T>?.toSuccessListResult(orDefault: ResultList<T> = emptyResult()): ResultList<T> =
    this?.run {
        if (this.isNotEmpty()) successResult(this) else orDefault
    } ?: orDefault

inline fun <reified T : Throwable> T.toErrorResult() =
    errorResult(this.message ?: this.cause?.message.orEmpty(), 0, this)

inline fun <reified T : Any> T.toEmptyResult() = SResult.Empty

inline fun <reified T : Throwable> T.toAlertResult(
    okTitle: Int? = null,
    noinline okHandler: UnitHandler? = null
) = alertResult(
    dialogMessage = this.message ?: this.cause?.message.orEmpty(),
    exception = this,
    okHandler = okHandler,
    okTitle = okTitle
)

inline fun <reified T : NavDirections> T.toNavigateResult() = navigateToResult(this)

// /-------- HANDLE FUNCTION
fun SResult<*>.handle() {
    if (isNeedHandle) isHandled = true
}

// /------- Mapper function
@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> ResultList<IConvertable>.mapListTo(): ResultList<O> {
    return when (this) {
        is SResult.Success -> {
            data.mapNotNull { it.convert<O>() }.mapToResult()
        }
        else -> this as ResultList<O>
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> ResultList<O>?.getList(): List<O> {
    return when (this) {
        is SResult.Success -> data
        else -> emptyList()
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any, reified I : IConvertable> SResult<I>.convertTo(): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convert<O>()?.toSuccessResult() ?: emptyResult()
        }
        else -> this as SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified O : Any, reified I : IConvertableSuspend> SResult<I>.convertToSuspend(): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convert<O>()?.toSuccessResult() ?: emptyResult()
        }
        else -> this as SResult<O>
    }
}

fun SResult.AbstractFailure.getMessage(): Any? {
    return (this.message?.takeIf { (it as? String)?.isNotEmpty() == true || ((it as? Int) != null && it > 0) }
        ?: (this.exception as? ISException)?.getErrorMessageResId()?.takeIf { it > 0 })
        ?: this.exception?.message
        ?: this.exception?.cause?.message
}

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.doIfSuccess(block: UnitHandler): SResult<I> {
    if (this is SResult.Success) block()
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.doIfError(block: UnitHandler): SResult<I> {
    if (this is SResult.AbstractFailure) block()
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.logIfError(textToLog: String): SResult<I> {
    if (this is SResult.AbstractFailure) logg { textToLog }
    return this
}

suspend inline fun <reified I : Any> SResult<I>.doIfSuccessSuspend(crossinline block: suspend () -> Unit): SResult<I> {
    if (this is SResult.Success) block()
    return this
}

// /--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.applyIfSuccess(block: InHandler<I>): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}

// /--- Inline Applying functions
inline fun <reified I> SResult<Any>.applyIfSuccessTyped(block: InHandler<I>): AnyResult {
    if (this is SResult.Success && this.data is I) block(this.data as I)
    return this
}

inline fun <reified I : Any> SResult<I>.applyIfError(block: InHandler<SResult.AbstractFailure>): SResult<I> {
    if (this is SResult.AbstractFailure) block(this)
    return this
}

inline fun <reified I : SResult<*>> SResult<*>.applyIfType(block: I.() -> Unit): SResult<*> {
    if (this::class == I::class) block(this as I)
    return this
}

@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
suspend inline fun <reified I : SResult<*>> SResult<*>.applyIfTypeSuspend(crossinline block: suspend I.() -> Unit): SResult<*> {
    if (this::class == I::class) block(this as I)
    return this
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfSuccess(block: I.() -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfSuccess(block: (I) -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

inline fun <reified T : List<*>> T.mapToResult(): SResult<T> {
    return this.takeIf { it.isNotEmpty() }?.toSuccessResult() ?: emptyResult()
}

inline fun <reified T : Any> T?.mapToResult(): SResult<T> {
    return this.toSuccessResult()
}


@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
suspend inline fun <reified I : Any> SResult<I>.applyIfSuccessSuspend(crossinline block: SInHandler<I>): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}

@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
suspend inline fun <reified I : Any> SResult<I>.applyIfEmptySuspend(crossinline block: SUnitHandler): SResult<I> {
    if (this is SResult.Empty) block()
    return this
}

@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
suspend inline fun <reified I : Any> SResult<I>.applyIfErrorSuspend(crossinline block: SInHandler<SResult.AbstractFailure>): SResult<I> {
    if (this is SResult.AbstractFailure) block(this)
    return this
}

@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any> SResult<*>.mapIfSuccessTyped(block: I.() -> SResult<*>): SResult<*> {
    return if (this is SResult.Success && this.data is I) block(this.data as I)
    else this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfSuccessSuspend(crossinline block: suspend I.() -> SResult<O>): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfErrorSuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.mapIfEmptySuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfSuccessSuspend(
    crossinline block: suspend (I) -> SResult<O>
): SResult<O> {
    return if (this is SResult.Success) block(this.data)
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfErrorSuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.AbstractFailure) block()
    else this as SResult<O>
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any, reified O : Any> SResult<I>.flatMapIfEmptySuspend(crossinline block: suspend () -> SResult<O>): SResult<O> {
    return if (this is SResult.Empty) block()
    else this as SResult<O>
}

inline fun <reified I : SResult<*>> SResult<*>.mapIfType(block: I.() -> SResult<*>): SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}

suspend inline fun <reified I : SResult<*>> SResult<*>.mapIfTypeSuspend(crossinline block: suspend I.() -> SResult<*>): SResult<*> {
    return if (this::class == I::class) block(this as I)
    else this
}

val <T : Any> SResult<T>.isSuccess: Boolean
    get() = this is SResult.Success<T>

val <T : Any> SResult<T>.isError: Boolean
    get() = this is SResult.AbstractFailure

fun <T : Any> SResult<T>?.orError(
    message: Any? = null,
    code: Int = 0,
    exception: Throwable? = null
): SResult<T> = this ?: SResult.AbstractFailure.Error(message, code, exception)

fun <T : Any> T?.orErrorResult(
    message: Any? = null,
    code: Int = 0,
    exception: Throwable? = null
): SResult<T> =
    this?.run { SResult.Success(this) } ?: SResult.AbstractFailure.Error(message, code, exception)