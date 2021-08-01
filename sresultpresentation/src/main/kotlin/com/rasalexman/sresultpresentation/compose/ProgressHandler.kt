package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.DoubleInHandler
import com.rasalexman.sresultpresentation.base.ILoadingHandler
import com.rasalexman.sresultpresentation.base.IProgressHandler

data class ProgressHandler(
    val onShowProgress: DoubleInHandler<Int, Any?>? = null,
    val loadingHandler: ILoadingHandler
) : IProgressHandler, ILoadingHandler by loadingHandler {

    class ProgressHandlerBuilder {
        var onShowProgress: DoubleInHandler<Int, Any?>? = null
        var loadingHandler: ILoadingHandler? = null

        fun build(): IProgressHandler {
            return ProgressHandler(
                onShowProgress = onShowProgress,
                loadingHandler = loadingHandler ?: loadingHandler {}
            )
        }
    }

    override fun showProgress(progress: Int, message: Any?) {
        onShowProgress?.invoke(progress, message)
    }
}
