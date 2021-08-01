package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresult.common.typealiases.OutHandler
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresultpresentation.base.IControlHandler
import com.rasalexman.sresultpresentation.base.IProgressHandler
import com.rasalexman.sresultpresentation.base.ISResultHandler

data class ControlHandler(
    val onBackPressed: OutHandler<Boolean>? = null,
    val onToolbarBackPressed: UnitHandler? = null,
    val onNextPressed: UnitHandler? = null,
    val progressHandler: IProgressHandler
) : IControlHandler, IProgressHandler by progressHandler {

    class ControllerHandlerBuilder : IHandlerBuilder<IControlHandler> {
        var onBackPressed: OutHandler<Boolean>? = null
        var onToolbarBackPressed: UnitHandler? = null
        var onNextPressed: UnitHandler? = null
        var progressHandler: IProgressHandler? = null

        override fun build(): IControlHandler {
            return ControlHandler(
                onBackPressed = onBackPressed,
                onToolbarBackPressed = onToolbarBackPressed,
                onNextPressed = onNextPressed,
                progressHandler = progressHandler ?: progressHandler {  }
            )
        }
    }

    override fun onBackPressed(): Boolean {
        return onBackPressed?.invoke() ?: false
    }

    override fun onToolbarBackPressed() {
        onToolbarBackPressed?.invoke()
    }

    override fun onNextPressed() {
        onNextPressed?.invoke()
    }
}
