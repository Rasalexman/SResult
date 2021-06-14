package com.rasalexman.sresultpresentation.fragments

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.base.IComplexHandler
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import java.lang.ref.WeakReference

interface IBaseFragment<out VM : IBaseViewModel> : IComplexHandler, Toolbar.OnMenuItemClickListener {

    val viewModel: VM?
    val layoutId: Int
    val toolbarTitle: String
    val toolbarSubTitle: String
    val toolbarTitleResId: Int?
    val centerToolbarTitle: Boolean
    val centerToolbarSubTitle: Boolean
    val toolbarMenuId: Int?

    val contentLayoutResId: Int get() = R.id.contentLayout
    val loadingLayoutResId: Int get() = R.id.loadingLayout
    val toolbarLayoutResId: Int get() = R.id.toolbarLayout

    val contentView: View?

    var weakContentRef: WeakReference<View>?
    var weakLoadingRef: WeakReference<View>?
    var weakToolbarRef: WeakReference<Toolbar>?

    /**
     * Main navigation host graph id for current element
     */
    val mainHostFragmentId: Int
        get() = R.id.mainHostFragment

    /**
     * Toolbar instance
     */
    val toolbarView: Toolbar? get() {
            return weakToolbarRef?.get() ?: contentView?.findViewById<Toolbar>(toolbarLayoutResId)?.also {
                weakToolbarRef = WeakReference(it)
            }
        }

    /**
     * Content Layout
     */
    val contentViewLayout: View? get() {
        return weakContentRef?.get() ?: contentView?.findViewById<Toolbar>(contentLayoutResId)?.also {
            weakContentRef = WeakReference(it)
        }
    }

    /**
     * Loading Layout
     */
    val loadingViewLayout: View? get() {
        return weakLoadingRef?.get() ?: contentView?.findViewById<View>(loadingLayoutResId)?.also {
            weakLoadingRef = WeakReference(it)
        }
    }
}