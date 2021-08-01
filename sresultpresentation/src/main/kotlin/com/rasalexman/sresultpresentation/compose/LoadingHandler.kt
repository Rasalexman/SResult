package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresultpresentation.base.ILoadingHandler
import com.rasalexman.sresultpresentation.base.ISResultHandler

data class LoadingHandler(
    val onHideLoading: UnitHandler? = null,
    val onShowLoading: UnitHandler? = null,
    val onResultHandler: ISResultHandler
) : ILoadingHandler, ISResultHandler by onResultHandler {

    class LoadingHandlerBuilder : IHandlerBuilder<ILoadingHandler> {
        var onHideLoading: UnitHandler? = null
        var onShowLoading: UnitHandler? = null
        var onResultHandler: ISResultHandler? = null

        override fun build(): ILoadingHandler {
            return LoadingHandler(
                onHideLoading = onHideLoading,
                onShowLoading = onShowLoading,
                onResultHandler = onResultHandler ?: sresultHandler {  }
            )
        }
    }

    override fun hideLoading() {
        onHideLoading?.invoke()
    }

    override fun showLoading() {
        onShowLoading?.invoke()
    }
}
