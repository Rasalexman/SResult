package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.ISResultHandler
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class SResultHandler(
    val onResultHandler: InHandler<SResult<*>>? = null
) : ISResultHandler {

    class SResultHandlerBuilder : IHandlerBuilder<ISResultHandler> {
        var onResultHandler: InHandler<SResult<*>>? = null

        override fun build(): ISResultHandler {
            return SResultHandler(
                onResultHandler = onResultHandler
            )
        }
    }

    override fun onResultHandler(result: SResult<*>) {
        onResultHandler?.invoke(result).or {
            onBaseResultHandler(result)
        }
    }
}