package com.rasalexman.sresultpresentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.typealiases.AnyResultLiveData
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

abstract class BaseDialogFragment<VM : IBaseViewModel> : AppCompatDialogFragment(),
    IBaseFragment<VM> {

    override val contentView: View?
        get() = this.view

    /**
     * Base view model
     */
    override val viewModel: VM? = null

    /**
     * Toolbar title
     */
    override val toolbarTitle: String = ""

    /**
     * Toolbar subtitle
     */
    override val toolbarSubTitle: String = ""

    /**
     * Toolbar title resourceId
     */
    override val toolbarTitleResId: Int? = null

    /**
     * Need to center toolbar title
     */
    override val centerToolbarTitle: Boolean = false

    /**
     * Need to center toolbar title
     */
    override val centerToolbarSubTitle: Boolean = false

    /**
     * Toolbar menu resId
     */
    @MenuRes
    override val toolbarMenuId: Int? = null

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
        addResultLiveDataObservers()
        addErrorLiveDataObservers()
        addNavigateLiveDataObserver()
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
        }
    }

    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) {
        toolbar.inflateMenu(menuResId)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return true
    }

    protected open fun addNavigateLiveDataObserver() {
        viewModel?.navigationLiveData?.apply(::observeNavigationLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun addResultLiveDataObservers() {
        (viewModel?.resultLiveData as? AnyResultLiveData)?.observe(this.viewLifecycleOwner) { result ->
            result.applyIf(!result.isHandled, ::onResultHandler)
        }
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun addErrorLiveDataObservers() {
        (viewModel?.supportLiveData as? AnyResultLiveData)?.observe(this.viewLifecycleOwner) { result ->
            result.applyIf(!result.isHandled, ::onResultHandler)
        }
    }

    /**
     * Observe only [SResult.NavigateResult] live data
     */
    protected open fun observeNavigationLiveData(data: LiveData<SResult.NavigateResult>) {
        onResultChange(data) { result ->
            result.applyIf(!result.isHandled, ::onResultHandler)
        }
    }

    override fun onResultHandler(result: ISResult<*>) {
        onBaseResultHandler(result)
    }

    override fun onBackPressed(): Boolean {
        dismiss()
        return false
    }

    override fun onToolbarBackPressed() = Unit
    override fun showEmptyLayout() = Unit
    override fun onNextPressed() = Unit
    override fun showProgress(progress: Int) = Unit

    override fun navigateTo(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    /**
     * Navigate by navResId
     */
    override fun navigateBy(navResId: Int) {
        findNavController().navigate(navResId)
    }

    /**
     * Navigate back by pop with navResId
     */
    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean) {
        findNavController().apply {
            navResId?.let {
                popBackStack(it, isInclusive)
            } ?: popBackStack()
        }
    }

    override fun showToast(message: Any, interval: Int) {
        hideKeyboard()
        hideLoading()
        toast(message, interval)
    }

    override fun showAlertDialog(
        message: Any,
        okTitle: Int?,
        okHandler: UnitHandler?
    ) {
        hideKeyboard()
        hideLoading()
        alert(message = message, okTitle = okTitle, okHandler = okHandler)
    }

    /**
     * Show loading state for [SResult.Loading]
     */
    override fun showLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    /**
     * Hide loading when it's needed
     */
    override fun hideLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }
    
    protected open fun initLayout() = Unit

    override fun showError(error: SResult.ErrorResult) {
        this.showResultError(error)
    }

    override fun showErrorLayout(
        imageResId: Int?,
        textResId: Int?,
        buttonTitleResId: Int?,
        tryAgainHandler: UnitHandler?
    ) = Unit

    override fun onDestroyView() {
        toolbarView?.setOnMenuItemClickListener(null)
        this.view.clearView()
        super.onDestroyView()
    }
}