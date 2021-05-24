package com.rasalexman.sresultpresentation.fragments

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavDirections
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

interface IBaseFragment<out VM : IBaseViewModel> : Toolbar.OnMenuItemClickListener {

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

    /**
     * Error Layout
     */
    val errorViewLayout: View?
        get() = contentView?.findViewById(R.id.errorLayout)

    fun hideLoading()
    fun showLoading()
    fun showEmptyLayout()

    fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int)

    fun onBackPressed(): Boolean
    fun onToolbarBackPressed()
    fun onNextPressed()

    fun onResultHandler(result: ISResult<*>)

    fun navigateTo(direction: NavDirections)
    fun navigateBy(navResId: Int)
    fun navigatePopTo(
        navResId: Int? = null,
        isInclusive: Boolean = false
    )

    fun showError(error: SResult.ErrorResult)
    fun showToast(message: Any, interval: Int = Toast.LENGTH_SHORT)
    fun showAlertDialog(
        message: Any,
        okTitle: Int? = null,
        okHandler: UnitHandler?
    )

    fun showErrorLayout(
        @DrawableRes imageResId: Int? = null,
        @StringRes textResId: Int? = null,
        @StringRes buttonTitleResId: Int? = null,
        tryAgainHandler: UnitHandler? = null
    )

    /**
     * Adapter function for start intent with activity
     */
    fun startActivityForResult(
        intent: Intent?,
        requestCode: Int
    )
}