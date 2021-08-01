package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.DoubleInHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.IProgressHandler
import com.rasalexman.sresultpresentation.base.IToastHandler
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class ToastHandler(
    val onShowToast: DoubleInHandler<Any?, Int>? = null,
    val progressHandler: IProgressHandler
) : IToastHandler, SResultHandler(), IProgressHandler by progressHandler {

    class ToastHandlerBuilder : IHandlerBuilder<IToastHandler> {
        var onShowToast: DoubleInHandler<Any?, Int>? = null
        var progressHandler: IProgressHandler? = null

        override fun build(): IToastHandler {
            return ToastHandler(
                onShowToast = onShowToast,
                progressHandler = progressHandler ?: progressHandler { }
            )
        }
    }

    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    override fun showToast(message: Any?, interval: Int) {
        onShowToast?.invoke(message, interval)
    }
}
