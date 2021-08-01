package com.rasalexman.sresultpresentation.compose

import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.rasalexman.sresult.common.typealiases.DoubleInHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.*
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class ComplexHandler(
    val emptyHandler: IEmptyHandler,
    val failureHandler: IFailureHandler,
    val navigateHandler: INavigateHandler,
    val successHandler: ISuccessHandler,
    val toastHandler: IToastHandler,
    val onInflateToolBarMenu: DoubleInHandler<Toolbar, Int>? = null,
    val onStartActivityForResult: DoubleInHandler<Intent?, Int>? = null
) : IComplexHandler, INavigateHandler by navigateHandler {

    class ComplexHandlerBuilder : IHandlerBuilder<IComplexHandler> {
        var navigateHandler: INavigateHandler? = null
        var successHandler: ISuccessHandler? = null
        var emptyHandler: IEmptyHandler? = null
        var failureHandler: IFailureHandler? = null
        var toastHandler: IToastHandler? = null

        var onInflateToolBarMenu: DoubleInHandler<Toolbar, Int>? = null
        var onStartActivityForResult: DoubleInHandler<Intent?, Int>? = null

        override fun build(): IComplexHandler {
            val navHandler = navigateHandler ?: navigateHandler { }
            val sucHandler = successHandler ?: successHandler { }
            val empHandler = emptyHandler ?: emptyHandler { }
            val flrHandler = failureHandler ?: failureHandler { }
            val tstHandler = toastHandler ?: toastHandler { }

            return ComplexHandler(
                navigateHandler = navHandler,
                successHandler = sucHandler,
                emptyHandler = empHandler,
                failureHandler = flrHandler,
                toastHandler = tstHandler,
                onInflateToolBarMenu = onInflateToolBarMenu,
                onStartActivityForResult = onStartActivityForResult
            )
        }
    }

    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) {
        onInflateToolBarMenu?.invoke(toolbar, menuResId)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        onStartActivityForResult?.invoke(intent, requestCode)
    }

    override fun showEmptyLayout() {
        emptyHandler.showEmptyLayout()
    }

    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        failureHandler.showFailure(error)
    }

    override fun showAlert(alert: SResult.AbstractFailure.Alert) {
        failureHandler.showAlert(alert)
    }

    override fun showSuccess(result: SResult.Success<*>) {
        successHandler.showSuccess(result)
    }

    override fun showToast(message: Any?, interval: Int) {
        toastHandler.showToast(message, interval)
    }
}