@file:Suppress("unused")

package com.rasalexman.sresult.common.extensions

import android.os.Bundle
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
inline fun <reified T : Any> Any.successResult(data: T): SResult<T> = SResult.Success<T>(data)

fun Any?.loadingResult(isNeedHandle: Boolean = true) = SResult.Loading(isNeedHandle)
fun Any?.emptyResult(isNeedHandle: Boolean = true) = if(isNeedHandle) {
    SResult.Empty
} else {
    SResult.EmptyUnhandled
}

fun Any.anySuccess() = SResult.AnySuccess

fun Any.progressResult(
    progress: Int,
    message: Any? = null,
    isPlus: Boolean = false,
    isNeedHandle: Boolean = true
) = SResult.Progress(
        progress = progress, message = message, isPlus = isPlus, isNeedHandle = isNeedHandle
    )

fun Int.toProgress(message: Any? = null, isNeedHandle: Boolean = true) =
    SResult.Progress(progress = this, message = message, isNeedHandle = isNeedHandle)

fun Any.navigateToResult(
    to: Any
) = SResult.NavigateResult.NavigateTo(to)

fun Any.navigateBy(
    navigateResourceId: Int
) = SResult.NavigateResult.NavigateBy(navigateResourceId)

fun Any.navigatePopTo(
    navigateResourceId: Int? = null,
    isInclusive: Boolean = false,
    backArgs: Bundle? = null
) = SResult.NavigateResult.NavigatePopTo(navigateResourceId, isInclusive).apply {
    this.args = backArgs
}

fun Any.navigatePop(backArgs: Bundle? = null) = SResult.NavigateResult.NavigatePop().apply {
    this.args = backArgs
}

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

inline fun <reified T : Any> T.toNavigateResult() = navigateToResult(this)

// /-------- HANDLE FUNCTION
fun SResult<*>.handle() {
    if (isNeedHandle) isHandled = true
}


@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> ResultList<O>?.getList(): List<O> {
    return when (this) {
        is SResult.Success -> data
        else -> emptyList()
    }
}

inline fun <reified O : Any> ResultList<O>.getMutableList(): MutableList<O> {
    return when (this) {
        is SResult.Success -> data.toMutableList()
        else -> mutableListOf()
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
inline fun <reified O : Any, reified I : IConvertableWithParams<O, I>> SResult<I>.convertTo(param: I): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convert(param)?.toSuccessResult() ?: emptyResult()
        }
        else -> this as SResult<O>
    }
}

inline fun <reified O : Any, reified I : IConvertable> FlowResult<I>.convertFlowTo(): FlowResult<O> {
    return this.map {
        it.convertTo()
    }
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified O : Any, reified I : IConvertableSuspend> SResult<I>.convertToSuspend(): SResult<O> {
    return when (this) {
        is SResult.Success -> {
            this.data.convert<O>().toSuccessResult(emptyResult())
        }
        else -> this as SResult<O>
    }
}

fun SResult.AbstractFailure.getMessage(): Any? {
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

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.doIfSuccess(block: InHandler<I>): SResult<I> {
    if (this is SResult.Success) block(this.data)
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.doIfError(block: InHandler<Throwable?>): SResult<I> {
    if (this is SResult.AbstractFailure) block(this.exception)
    return this
}

///--- Inline Applying functions
inline fun <reified I : Any> SResult<I>.logIfError(textToLog: String): SResult<I> {
    if (this is SResult.AbstractFailure) logg { textToLog }
    return this
}

@Suppress("UNCHECKED_CAST")
suspend inline fun <reified I : Any> SResult<I>.doIfSuccessSuspend(crossinline block: suspend (I) -> SResult<I>): SResult<I> {
    return if (this is SResult.Success) block(this.data)
    else this
}

val <T : Any> SResult<T>.isSuccess: Boolean
    get() = this is SResult.Success<T>

val <T : Any> SResult<T>.isError: Boolean
    get() = this is SResult.AbstractFailure

val <T : Any> SResult<T>.isEmpty: Boolean
    get() = this is SResult.Empty

val <T : Any> SResult<T>.isLoading: Boolean
    get() = this is SResult.Loading

val <T : Any> SResult<T>.isToast: Boolean
    get() = this is SResult.Toast

fun SResult.AbstractFailure.getErrorMessage(): Any {
    return (this.message as? String)?.takeIf { it.isNotEmpty() }.or {
        (this.message as? Int).or { this.exception?.message.or { this.exception?.localizedMessage }.orEmpty() }
    }
}

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

@Suppress("UNCHECKED_CAST")
inline fun <reified O : Any> Flow<List<O>>.toFlowSuccess(): FlowResultList<O> {
    return this.map { it.toSuccessResult() }.buffer(1)
}

inline fun <reified T : Any> ResultList<T>.size() = this.data?.size.orZero()

inline fun <reified T : Any> Flow<T>.mapToFlowResult(): FlowResult<T> =
    this.map { it.toSuccessResult() }