package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.IFailureHandler
import com.rasalexman.sresultpresentation.base.IProgressHandler
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class FailureHandler(
    val onShowFailure: InHandler<SResult.AbstractFailure.Failure>? = null,
    val onShowAlert: InHandler<SResult.AbstractFailure.Alert>? = null,
    val progressHandler: IProgressHandler
) : IFailureHandler, IProgressHandler by progressHandler  {

    class FailureHandlerBuilder : IHandlerBuilder<IFailureHandler> {
        var onShowFailure: InHandler<SResult.AbstractFailure.Failure>? = null
        var onShowAlert: InHandler<SResult.AbstractFailure.Alert>? = null
        var progressHandler: IProgressHandler? = null

        override fun build(): IFailureHandler {
            return FailureHandler(
                onShowFailure = onShowFailure,
                onShowAlert = onShowAlert,
                progressHandler = progressHandler ?: progressHandler {  }
            )
        }
    }

    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        onShowFailure?.invoke(error)
    }

    override fun showAlert(alert: SResult.AbstractFailure.Alert) {
        onShowAlert?.invoke(alert)
    }

}
