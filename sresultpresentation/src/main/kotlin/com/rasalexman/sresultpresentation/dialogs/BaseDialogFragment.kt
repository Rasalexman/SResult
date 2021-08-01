package com.rasalexman.sresultpresentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.getMessage
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IResultViewModel
import java.lang.ref.WeakReference

abstract class BaseDialogFragment<VM : IResultViewModel> : AppCompatDialogFragment(),
    IBaseFragment<VM> {

    override val contentView: View?
        get() = this.view

    override var weakContentRef: WeakReference<View>? = null
    override var weakLoadingRef: WeakReference<View>? = null
    override var weakToolbarRef: WeakReference<Toolbar>? = null

    /**
     * Base view model
     */
    override val viewModel: VM? = null

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolbar()
        initLayout()
        addViewModelObservers(viewModel)
    }

    /**
     * For Add Custom Toolbar instance as SupportActionBar
     */
    protected open fun showToolbar() {
        toolbarView?.let { toolbar ->
            toolbarMenuId?.let {
                inflateToolBarMenu(toolbar, it)
            }
            initToolbarTitle(toolbar)
            initToolbarNavigationIcon(toolbar)
        }
    }

    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) {
        toolbar.inflateMenu(menuResId)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return true
    }

    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    override fun onBackPressed(): Boolean {
        dismiss()
        return false
    }

    /**
     * On Toolbar back button Pressed
     */
    override fun onToolbarBackPressed() {
        this.findNavController().popBackStack()
    }

    override fun showEmptyLayout() = Unit
    override fun onNextPressed() = Unit
    override fun showProgress(progress: Int, message: Any?) = Unit
    override fun showNavigationError(e: Exception?, navResId: Int?) = Unit
    override fun showSuccess(result: SResult.Success<*>) = Unit
    override fun onAnyDataHandler(data: Any?) = Unit

    /**
     * Navigate by direction [NavDirections]
     */
    override fun navigateTo(direction: Any) {
        (direction as? NavDirections)?.let {
            this.navigateTo(context, findNavController(), direction)
        }
    }

    /**
     * Navigate by navResId [Int]
     */
    override fun navigateBy(navResId: Int) {
        this.navigateBy(context, findNavController(), navResId)
    }

    /**
     * Navigate back by pop with navResId
     */
    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean, backArgs: Bundle?) {
        this.navigatePopTo(context, findNavController(), navResId, isInclusive, backArgs)
    }

    /**
     * Navigate to pop on context navigator
     */
    override fun navigatePop(backArgs: Bundle?) {
        this.navigatePop(context, backArgs)
    }

    override fun showToast(message: Any?, interval: Int) {
        hideKeyboard()
        hideLoading()
        toast(message, interval)
    }

    /**
     * Show loading state for [SResult.Loading]
     */
    override fun showLoading() {
        hideKeyboard()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    /**
     * Hide loading when it's needed
     */
    override fun hideLoading() {
        hideKeyboard()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }

    protected open fun initLayout() = Unit

    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        this.toast(error.getMessage(), error.interval)
    }

    override fun showAlert(alert: SResult.AbstractFailure.Alert) {
        this.alert(
            message = alert.getMessage(),
            dialogTitle = alert.dialogTitle,
            okTitle = alert.okTitle,
            cancelTitle = alert.cancelTitle,
            cancelHandler = alert.cancelHandler,
            okHandler = alert.okHandler,
            showCancel = alert.cancelHandler != null
        )
    }

    override fun toolbarTitleHandler(title: String) {
        toolbarView?.setupToolbarTitle(title, toolbarTitleResId, centerToolbarTitle)
    }
    override fun toolbarSubTitleHandler(subtitle: String) {
        toolbarView?.setupToolbarSubtitle(subtitle, centerToolbarTitle)
    }

    override fun onDestroyView() {
        this.clear(this.viewLifecycleOwner)
        this.view.clearView()
        (view as? ViewGroup)?.removeAllViews()
        super.onDestroyView()
    }
}