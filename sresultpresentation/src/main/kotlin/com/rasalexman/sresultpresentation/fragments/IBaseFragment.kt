package com.rasalexman.sresultpresentation.fragments

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.base.IComplexHandler
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

interface IBaseFragment<out VM : IBaseViewModel> : IComplexHandler, Toolbar.OnMenuItemClickListener {

    val viewModel: VM?
    val layoutId: Int
    val toolbarTitle: String
    val toolbarSubTitle: String
    val toolbarTitleResId: Int?
    val centerToolbarTitle: Boolean
    val centerToolbarSubTitle: Boolean
    val toolbarMenuId: Int?

    val contentView: View?

    /**
     * Main navigation host graph id for current element
     */
    val mainHostFragmentId: Int
        get() = R.id.mainHostFragment

    /**
     * Toolbar instance
     */
    val toolbarView: Toolbar?
        get() = contentView?.findViewById(R.id.toolbarLayout)

    /**
     * Content Layout
     */
    val contentViewLayout: View?
        get() = contentView?.findViewById(R.id.contentLayout)

    /**
     * Loading Layout
     */
    val loadingViewLayout: View?
        get() = contentView?.findViewById(R.id.loadingLayout)
}