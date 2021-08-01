package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.IEmptyHandler
import com.rasalexman.sresultpresentation.base.IProgressHandler
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class EmptyHandler(
    val onShowEmpty: UnitHandler? = null,
    val progressHandler: IProgressHandler
) : IEmptyHandler, SResultHandler(), IProgressHandler by progressHandler {

    class EmptyHandlerBuilder : IHandlerBuilder<IEmptyHandler> {
        var onShowEmptyLayout: UnitHandler? = null
        var progressHandler: IProgressHandler? = null

        override fun build(): IEmptyHandler {
            return EmptyHandler(
                onShowEmpty = onShowEmptyLayout,
                progressHandler = progressHandler ?: progressHandler {  }
            )
        }
    }

    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    override fun showEmptyLayout() {
        onShowEmpty?.invoke()
    }
}
