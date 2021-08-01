package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresultpresentation.base.*

fun sresultHandler(block: SResultHandler.SResultHandlerBuilder.() -> Unit): ISResultHandler {
    return SResultHandler.SResultHandlerBuilder().apply(block).build()
}

fun loadingHandler(block: LoadingHandler.LoadingHandlerBuilder.() -> Unit): ILoadingHandler {
    return LoadingHandler.LoadingHandlerBuilder().apply(block).build()
}

fun progressHandler(block: ProgressHandler.ProgressHandlerBuilder.() -> Unit): IProgressHandler {
    return ProgressHandler.ProgressHandlerBuilder().apply(block).build()
}

fun controlHandler(block: ControlHandler.ControllerHandlerBuilder.() -> Unit): IControlHandler {
    return ControlHandler.ControllerHandlerBuilder().apply(block).build()
}

fun failureHandler(block: FailureHandler.FailureHandlerBuilder.() -> Unit): IFailureHandler {
    return FailureHandler.FailureHandlerBuilder().apply(block).build()
}

fun navigateHandler(block: NavigateHandler.NavigateHandlerBuilder.() -> Unit): INavigateHandler {
    return NavigateHandler.NavigateHandlerBuilder().apply(block).build()
}

fun toastHandler(block: ToastHandler.ToastHandlerBuilder.() -> Unit): IToastHandler {
    return ToastHandler.ToastHandlerBuilder().apply(block).build()
}

fun emptyHandler(block: EmptyHandler.EmptyHandlerBuilder.() -> Unit): IEmptyHandler {
    return EmptyHandler.EmptyHandlerBuilder().apply(block).build()
}

fun successHandler(block: SuccessHandler.SuccessHandlerBuilder.() -> Unit): ISuccessHandler {
    return SuccessHandler.SuccessHandlerBuilder().apply(block).build()
}

fun complexHandler(block: ComplexHandler.ComplexHandlerBuilder.() -> Unit): IComplexHandler {
    return ComplexHandler.ComplexHandlerBuilder().apply(block).build()
}

fun resultResolver(block: ResultResolver.ResultResolverBuilder.() -> Unit): IComplexHandler {
    return ResultResolver.ResultResolverBuilder().apply(block).build()
}