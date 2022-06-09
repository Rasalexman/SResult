@file:Suppress("unused")

package com.rasalexman.sresult.common.extensions

import com.rasalexman.sresult.common.typealiases.*
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresult.data.exception.ISException
import com.rasalexman.sresult.models.IConvertable
import com.rasalexman.sresult.models.IConvertableSuspend
import com.rasalexman.sresult.models.IConvertableWithParams
import com.rasalexman.sresult.models.convert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map

// /------ ViewResult extensions
inline fun <reified T : Any> Any.successResult(data: T): com.rasalexman.sresult.data.dto.SResult<T> = com.rasalexman.sresult.data.dto.SResult.Success(data)

fun Any?.loadingResult(isNeedHandle: Boolean = true) = com.rasalexman.sresult.data.dto.SResult.Loading(isNeedHandle)
fun Any?.emptyResult(isNeedHandle: Boolean = true) = if(isNeedHandle) {
    com.rasalexman.sresult.data.dto.SResult.Empty
} else {
    com.rasalexman.sresult.data.dto.SResult.EmptyUnhandled
}

fun Any.anySuccess() = com.rasalexman.sresult.data.dto.SResult.AnySuccess

fun Any.progressResult(
    progress: Int,
    message: Any? = null,
    isPlus: Boolean = false,
    isNeedHandle: Boolean = true
) = com.rasalexman.sresult.data.dto.SResult.Progress(
        progress = progress, message = message, isPlus = isPlus, isNeedHandle = isNeedHandle
    )

fun Int.toProgress(message: Any? = null, isNeedHandle: Boolean = true) =
    com.rasalexman.sresult.data.dto.SResult.Progress(progress = this, message = message, isNeedHandle = isNeedHandle)

fun Any.navigateToResult(
    to: Any
) = com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigateTo(to)

fun Any.navigateBy(
    navigateResourceId: Int
) = com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigateBy(navigateResourceId)

fun Any.navigatePopTo(
    navigateResourceId: Int? = null,
    isInclusive: Boolean = false,
    backArgs: Map<String, Any?>? = null
) = com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigatePopTo(navigateResourceId, isInclusive).apply {
    this.args = backArgs
}

fun Any.navigatePop(backArgs: Map<String, Any?>? = null) = com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigatePop().apply {
    this.args = backArgs
}

fun Any.navigateBackResult() =
    com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigateBack()

fun Any.navigateNextResult() =
    com.rasalexman.sresult.data.dto.SResult.NavigateResult.NavigateNext()

fun Any.errorResult(
    message: String = "",
    code: Int = -1,
    exception: Throwable? = null
) = com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Error(message, code, exception)

fun Any.alertResult(
    dialogMessage: Any? = null,
    exception: Throwable? = null,
    dialogTitle: Any? = null,
    okHandler: UnitHandler? = null,
    cancelHandler: UnitHandler? = null,
    okTitle: Any? = null,
    cancelTitle: Any? = null
) = com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Alert(
    message = dialogMessage,
    exception = exception,
    dialogTitle = dialogTitle,
    okHandler = okHandler,
    cancelHandler = cancelHandler,
    okTitle = okTitle,
    cancelTitle = cancelTitle
)

fun Any.toastResult(message: Any?) = com.rasalexman.sresult.data.dto.SResult.Toast(message)

// /-------- toState Convertables
inline fun <reified T : Any> T?.toSuccessResult(orDefault: com.rasalexman.sresult.data.dto.SResult<T> = emptyResult()): com.rasalexman.sresult.data.dto.SResult<T> =
    this?.let {
        successResult(it)
    } ?: orDefault

inline fun <reified T : Any> List<T>?.toSuccessListResult(orDefault: ResultList<T> = emptyResult()): ResultList<T> =
    this?.run {
        if (this.isNotEmpty()) successResult(this) else orDefault
    } ?: orDefault

inline fun <reified T : Throwable> T.toErrorResult() =
    errorResult(this.message ?: this.cause?.message.orEmpty(), 0, this)

inline fun <reified T : Any> T.toEmptyResult() = com.rasalexman.sresult.data.dto.SResult.Empty

inline fun <reified T : Throwable> T.toAlertResult(
    okTitle: Int? = null,
    noinline okHandler: UnitHandler? = null
) = alertResult(
    dialogMessage = this.message ?: this.cause?.message.orEmpty(),
    exception = this,
    okHandler = okHandler,
    okTitle = okTitle
)

inline fun <reified T : Any> T.toNavigateResult() = navigateToResult(this)

// /-------- HANDLE FUNCTION
fun com.rasalexman.sresult.data.dto.SResult<*>.handle() {
    if (isNeedHandle) isHandled = true
}


@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> ResultList<O>?.getList(): List<O> {
    return when (this) {
        is com.rasalexman.sresult.data.dto.SResult.Success -> data
        else -> emptyList()
    }
}

inline fun <reified O : Any> ResultList<O>.getMutableList(): MutableList<O> {
    return when (this) {
        is com.rasalexman.sresult.data.dto.SResult.Success -> data.toMutableList()
        else -> mutableListOf()
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any, reified I : IConvertable> com.rasalexman.sresult.data.dto.SResult<I>.convertTo(): com.rasalexman.sresult.data.dto.SResult<O> {
    return when (this) {
        is com.rasalexman.sresult.data.dto.SResult.Success -> {
            this.data.convert<O>().toSuccessResult()
        }
        else -> this as com.rasalexman.sresult.data.dto.SResult<O>
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any, reified S: Any, reified I : IConvertableWithParams<O, S>> com.rasalexman.sresult.data.dto.SResult<I>.convertTo(param: S): com.rasalexman.sresult.data.dto.SResult<O> {
    return when (this) {
        is com.rasalexman.sresult.data.dto.SResult.Success -> {
            this.data.convert(param).toSuccessResult()
        }
        else -> this as com.rasalexman.sresult.data.dto.SResult<O>
    }
}

inline fun <reified O : Any, reified I : IConvertable> FlowResult<I>.convertFlowTo(): FlowResult<O> {
    return this.map {
        it.convertTo()
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified O : Any, reified I : IConvertableSuspend> com.rasalexman.sresult.data.dto.SResult<I>.convertToSuspend(): com.rasalexman.sresult.data.dto.SResult<O> {
    return when (this) {
        is com.rasalexman.sresult.data.dto.SResult.Success -> {
            this.data.convert<O>().toSuccessResult(emptyResult())
        }
        else -> this as com.rasalexman.sresult.data.dto.SResult<O>
    }
}

fun com.rasalexman.sresult.data.dto.SResult.AbstractFailure.getMessage(): Any? {
    return this.message?.takeIf { (it as? String)?.isNotEmpty() == true || ((it as? Int) != null && it > 0) }
        .or {
            (this.exception as? ISException)?.getErrorMessageResId()?.takeIf { it > 0 }
        }.or {
            this.exception?.message
        }.or {
            this.exception?.cause?.message
        }.or {
            this.exception?.localizedMessage
        }
}


fun com.rasalexman.sresult.data.dto.SResult.AbstractFailure.getStringMessage(): String {
    return (this.message as? String)?.takeIf { it.isNotEmpty() }.or {
        this.exception?.message.or { this.exception?.localizedMessage }.orEmpty()
    }
}

///--- Inline Applying functions
inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfSuccess(block: InHandler<I>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfError(block: InHandler<Throwable?>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this.exception)
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfEmpty(block: UnitHandler): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.logIfError(textToLog: String): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) logg { textToLog }
    return this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfSuccessSuspend(crossinline block: SInHandler<I>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Success) block(this.data)
    return this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfEmptySuspend(crossinline block: SUnitHandler): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.Empty) block()
    return this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> com.rasalexman.sresult.data.dto.SResult<I>.doIfErrorSuspend(crossinline block: SInHandler<Throwable?>): com.rasalexman.sresult.data.dto.SResult<I> {
    if (this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure) block(this.exception)
    return this
}

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isSuccess: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.Success<T>

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isError: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.AbstractFailure

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isEmpty: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.Empty

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isLoading: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.Loading

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isProgress: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.Progress

val <T : Any> com.rasalexman.sresult.data.dto.SResult<T>.isToast: Boolean
    get() = this is com.rasalexman.sresult.data.dto.SResult.Toast

fun com.rasalexman.sresult.data.dto.SResult.AbstractFailure.getErrorMessage(): Any {
    return (this.message as? String)?.takeIf { it.isNotEmpty() }.or {
        (this.message as? Int).or { this.exception?.message.or { this.exception?.localizedMessage }.orEmpty() }
    }
}

fun <T : Any> com.rasalexman.sresult.data.dto.SResult<T>?.orError(
    message: Any? = null,
    code: Int = 0,
    exception: Throwable? = null
): com.rasalexman.sresult.data.dto.SResult<T> = this ?: com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Error(message, code, exception)

fun <T : Any> T?.toSuccessOrErrorResult(
    message: Any? = null,
    code: Int = 0,
    exception: Throwable? = null
): com.rasalexman.sresult.data.dto.SResult<T> =
    this?.run { com.rasalexman.sresult.data.dto.SResult.Success(this) } ?: com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Error(message, code, exception)

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> Flow<List<O>>.toFlowSuccess(): FlowResultList<O> {
    return this.map { it.toSuccessResult() }.buffer(1)
}

inline fun <reified T : Any> ResultList<T>.size() = this.data?.size.orZero()

inline fun <reified T : Any> Flow<T>.mapToFlowResult(noinline mapper: SInSameOutHandler<T>? = null): FlowResult<T> =
    this.map { (mapper?.invoke(it) ?: it).toSuccessResult() }