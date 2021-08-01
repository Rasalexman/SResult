package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.ILoadingHandler
import com.rasalexman.sresultpresentation.base.ISuccessHandler

data class SuccessHandler(
    val onShowSuccess: InHandler<SResult.Success<*>>? = null,
    val progressHandler: ILoadingHandler
) : ISuccessHandler, ILoadingHandler by progressHandler {

    class SuccessHandlerBuilder : IHandlerBuilder<ISuccessHandler> {

        var onShowSuccess: InHandler<SResult.Success<*>>? = null
        var progressHandler: ILoadingHandler? = null

        override fun build(): ISuccessHandler {
            return SuccessHandler(
                onShowSuccess = onShowSuccess,
                progressHandler = progressHandler ?: loadingHandler { }
            )
        }
    }

    override fun showSuccess(result: SResult.Success<*>) {
        onShowSuccess?.invoke(result)
    }

}
