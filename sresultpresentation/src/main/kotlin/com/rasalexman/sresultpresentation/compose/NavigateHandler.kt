package com.rasalexman.sresultpresentation.compose

import android.os.Bundle
import com.rasalexman.sresult.common.typealiases.DoubleInHandler
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.common.typealiases.TripleInHandler
import com.rasalexman.sresultpresentation.base.IControlHandler
import com.rasalexman.sresultpresentation.base.INavigateHandler

data class NavigateHandler(
    val onNavigateTo: InHandler<Any>? = null,
    val onNavigateBy: InHandler<Int>? = null,
    val onNavigatePopTo: TripleInHandler<Int?, Boolean, Bundle?>? = null,
    val onNavigatePop: InHandler<Bundle?>? = null,
    val onShowNavigationError: DoubleInHandler<Exception?, Int?>? = null,
    val controlHandler: IControlHandler
) : INavigateHandler, IControlHandler by controlHandler {

    class NavigateHandlerBuilder : IHandlerBuilder<INavigateHandler> {
        var onNavigateTo: InHandler<Any>? = null
        var onNavigateBy: InHandler<Int>? = null
        var onNavigatePopTo: TripleInHandler<Int?, Boolean, Bundle?>? = null
        var onNavigatePop: InHandler<Bundle?>? = null
        var onShowNavigationError: DoubleInHandler<Exception?, Int?>? = null
        var controlHandler: IControlHandler? = null

        override fun build(): INavigateHandler {
            return NavigateHandler(
                onNavigateTo = onNavigateTo,
                onNavigateBy = onNavigateBy,
                onNavigatePopTo = onNavigatePopTo,
                onNavigatePop = onNavigatePop,
                onShowNavigationError = onShowNavigationError,
                controlHandler = controlHandler ?: controlHandler { }
            )
        }
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

}
