package com.rasalexman.sresultpresentation.compose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.rasalexman.sresult.common.typealiases.*
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.IComplexHandler
import com.rasalexman.sresultpresentation.extensions.onBaseResultHandler

data class ResultResolver(
    val onInflateToolBarMenu: DoubleInHandler<Toolbar, Int>? = null,
    val onStartActivityForResult: DoubleInHandler<Intent?, Int>? = null,
    val onBackPressed: OutHandler<Boolean>? = null,
    val onToolbarBackPressed: UnitHandler? = null,
    val onNextPressed: UnitHandler? = null,
    val onShowProgress: DoubleInHandler<Int, Any?>? = null,
    val onHideLoading: UnitHandler? = null,
    val onShowLoading: UnitHandler? = null,
    val onShowEmpty: UnitHandler? = null,
    val onShowFailure: InHandler<SResult.AbstractFailure.Failure>? = null,
    val onShowAlert: InHandler<SResult.AbstractFailure.Alert>? = null,
    val onNavigateTo: InHandler<Any>? = null,
    val onNavigateBy: InHandler<Int>? = null,
    val onNavigatePopTo: TripleInHandler<Int?, Boolean, Bundle?>? = null,
    val onNavigatePop: InHandler<Bundle?>? = null,
    val onShowNavigationError: DoubleInHandler<Exception?, Int?>? = null,
    val onShowSuccess: InHandler<SResult.Success<*>>? = null,
    val onShowToast: DoubleInHandler<Any?, Int>? = null
) : IComplexHandler {

    class ResultResolverBuilder : IHandlerBuilder<IComplexHandler> {
        var onInflateToolBarMenu: DoubleInHandler<Toolbar, Int>? = null
        var onStartActivityForResult: DoubleInHandler<Intent?, Int>? = null
        var onBackPressed: OutHandler<Boolean>? = null
        var onToolbarBackPressed: UnitHandler? = null
        var onNextPressed: UnitHandler? = null
        var onShowProgress: DoubleInHandler<Int, Any?>? = null
        var onHideLoading: UnitHandler? = null
        var onShowLoading: UnitHandler? = null
        var onShowEmpty: UnitHandler? = null
        var onShowFailure: InHandler<SResult.AbstractFailure.Failure>? = null
        var onShowAlert: InHandler<SResult.AbstractFailure.Alert>? = null
        var onNavigateTo: InHandler<Any>? = null
        var onNavigateBy: InHandler<Int>? = null
        var onNavigatePopTo: TripleInHandler<Int?, Boolean, Bundle?>? = null
        var onNavigatePop: InHandler<Bundle?>? = null
        var onShowNavigationError: DoubleInHandler<Exception?, Int?>? = null
        var onShowSuccess: InHandler<SResult.Success<*>>? = null
        var onShowToast: DoubleInHandler<Any?, Int>? = null

        override fun build(): IComplexHandler {
            return ResultResolver(
                onInflateToolBarMenu = onInflateToolBarMenu,
                onStartActivityForResult = onStartActivityForResult,
                onBackPressed = onBackPressed,
                onToolbarBackPressed = onToolbarBackPressed,
                onNextPressed = onNextPressed,
                onShowProgress = onShowProgress,
                onHideLoading = onHideLoading,
                onShowLoading = onShowLoading,
                onShowEmpty = onShowEmpty,
                onShowFailure = onShowFailure,
                onShowAlert = onShowAlert,
                onNavigateTo = onNavigateTo,
                onNavigateBy = onNavigateBy,
                onNavigatePopTo = onNavigatePopTo,
                onNavigatePop = onNavigatePop,
                onShowNavigationError = onShowNavigationError,
                onShowSuccess = onShowSuccess,
                onShowToast = onShowToast
            )
        }
    }

    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) {
        onInflateToolBarMenu?.invoke(toolbar, menuResId)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        onStartActivityForResult?.invoke(intent, requestCode)
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

    override fun showProgress(progress: Int, message: Any?) {
        onShowProgress?.invoke(progress, message)
    }

    override fun hideLoading() {
        onHideLoading?.invoke()
    }

    override fun showLoading() {
        onShowLoading?.invoke()
    }

    override fun onResultHandler(result: SResult<*>) {
        this.onBaseResultHandler(result)
    }

    override fun showEmptyLayout() {
        onShowEmpty?.invoke()
    }

    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        onShowFailure?.invoke(error)
    }

    override fun showAlert(alert: SResult.AbstractFailure.Alert) {
        onShowAlert?.invoke(alert)
    }

    override fun navigateTo(direction: Any) {
        onNavigateTo?.invoke(direction)
    }

    override fun navigateBy(navResId: Int) {
        onNavigateBy?.invoke(navResId)
    }

    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean, backArgs: Bundle?) {
        onNavigatePopTo?.invoke(navResId, isInclusive, backArgs)
    }

    override fun navigatePop(backArgs: Bundle?) {
        onNavigatePop?.invoke(backArgs)
    }

    override fun showNavigationError(e: Exception?, navResId: Int?) {
        onShowNavigationError?.invoke(e, navResId)
    }

    override fun showSuccess(result: SResult.Success<*>) {
        onShowSuccess?.invoke(result)
    }

    override fun showToast(message: Any?, interval: Int) {
        onShowToast?.invoke(message, interval)
    }
}