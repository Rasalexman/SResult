package com.rasalexman.sresultpresentation.databinding

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.common.typealiases.AnyResultLiveData
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

abstract class BaseBindingLayout<VB : ViewDataBinding, VM : BaseViewModel> : FrameLayout,
    LifecycleOwner, IBaseFragment<VM>, IBaseBindingFragment<VB, VM> {

    open val contentLayoutResId: Int = R.id.contentLayout
    open val loadingLayoutResId: Int = R.id.loadingLayout
    open val errorLayoutResId: Int = R.id.errorLayout

    override val contentView: View?
        get() = this

    /**
     * Content Layout
     */
    override val contentViewLayout: View? get() {
        return this.findViewById(contentLayoutResId)
    }

    /**
     * Loading Layout
     */
    override val loadingViewLayout: View? get() {
        return this.findViewById(loadingLayoutResId)
    }

    /**
     * Error Layout
     */
    override val errorViewLayout: View? get() {
        return this.findViewById(errorLayoutResId)
    }

    override var binding: VB? = null

    /**
     * Fragment ViewModel instance
     */
    override val viewModel: VM? = null

    constructor(context: Context) : super(context) {
        createLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        createLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createLayout(context, attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        createLayout(context, attrs, defStyleAttr)
    }

    private fun createLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val view = DataBindingUtil.inflate<VB>(LayoutInflater.from(context), layoutId, this, true).also {
            binding = it
        }.root
        applyAdditionalParameters(context, attrs, defStyleAttr, defStyleRes)
        initLayout(view)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding?.let {
            it.lifecycleOwner = this
            it.setVariable(BR.vm, viewModel)
            initBinding(it)
        }
        addErrorLiveDataObservers()
        addResultLiveDataObservers()
        addNavigateLiveDataObserver()
    }

    override fun onDetachedFromWindow() {
        binding?.unbind()
        val lifecycleOwner = this
        viewModel?.apply {
            resultLiveData?.removeObservers(lifecycleOwner)
            supportLiveData.removeObservers(lifecycleOwner)
            navigationLiveData.removeObservers(lifecycleOwner)
        }
        super.onDetachedFromWindow()
    }

    protected open fun applyAdditionalParameters(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) = Unit

    override fun initBinding(binding: VB) = Unit
    protected open fun initLayout(view: View) = Unit

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun addResultLiveDataObservers() {
        (viewModel?.resultLiveData as? AnyResultLiveData)?.apply(::observeResultLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    protected open fun addErrorLiveDataObservers() {
        (viewModel?.supportLiveData as? AnyResultLiveData)?.apply(::observeResultLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult.NavigateResult] event
     */
    protected open fun addNavigateLiveDataObserver() {
        viewModel?.navigationLiveData?.apply(::observeNavigationLiveData)
    }

    /**
     * Observe only [SResult] live data
     */
    protected open fun observeResultLiveData(data: LiveData<AnyResult>) {
        onResultChange(data) { result ->
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

    /**
     * Base Result handler function
     */
    override fun onResultHandler(result: ISResult<*>) {
        onBaseResultHandler(result)
    }

    /**
     * Navigate by direction [NavDirections]
     */
    override fun navigateTo(direction: NavDirections) {
        try {
            this.findNavController().navigate(direction)
        } catch (e: Exception) {
            loggE(e, "There is no navigation direction from ${this::class.java.simpleName} with id = ${direction.actionId}")
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        R.id.mainHostFragment
                    ).navigate(direction)
                } ?: showNavigationError(null, direction.actionId)
            } catch (e: Exception) {
                showNavigationError(e, direction.actionId)
            }
        }
    }

    /**
     * Navigate by navResId [Int]
     */
    override fun navigateBy(navResId: Int) {
        try {
            this.findNavController().navigate(navResId)
        } catch (e: Exception) {
            loggE(e, "There is no navigation direction from ${this::class.java.simpleName} with id = $navResId")
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        R.id.mainHostFragment
                    ).navigate(navResId)
                } ?: showNavigationError(null, navResId)
            } catch (e: Exception) {
                showNavigationError(e, navResId)
            }
        }
    }

    /**
     * Navigate back by pop with navResId
     */
    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean) {
        try {
            this.findNavController().apply {
                navResId?.let {
                    popBackStack(it, isInclusive)
                } ?: popBackStack()
            }
        } catch (e: Exception) {
            loggE(e, "There is no navigation direction from ${this::class.java.simpleName} with id = $navResId")
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        R.id.mainHostFragment
                    ).apply {
                        navResId?.let { currentNavID ->
                            popBackStack(currentNavID, isInclusive)
                        } ?: popBackStack()
                    }
                } ?: showNavigationError(e, navResId)
            } catch (e: Exception) {
                showNavigationError(e, navResId)
            }
        }
    }

    private fun showNavigationError(e: Exception? = null, navResId: Int?) {
        loggE(e, "There is no navigation direction from ${this::class.java.simpleName} with contentViewLayout id = $navResId")
        showToast(R.string.error_internal)
    }

    override fun showToast(message: Any, interval: Int) {
        hideKeyboard()
        hideLoading()
        context?.toast(message, interval)
    }

    override fun showAlertDialog(
        message: Any,
        okTitle: Int?,
        okHandler: UnitHandler?
    ) {
        hideKeyboard()
        hideLoading()
        context?.alert(message = message, okTitle = okTitle, okHandler = okHandler)
    }

    override fun showLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    override fun hideLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }

    override fun showProgress(progress: Int) = Unit

    override fun showError(error: SResult.ErrorResult) {
        this.showResultError(error)
    }

    override fun showErrorLayout(
        imageResId: Int?,
        textResId: Int?,
        buttonTitleResId: Int?,
        tryAgainHandler: UnitHandler?
    ) = Unit

    /**
     * get view [Lifecycle] from its [Context]
     */
    override fun getLifecycle(): Lifecycle {
        return context.getOwner<LifecycleOwner>().lifecycle
    }

    /**
     *  get [ViewModelStoreOwner]
     */
    protected fun getViewModelStoreOwner(): ViewModelStoreOwner {
        return context.getOwner()
    }

    ///------- UNUSED SECTION ----///
    override val toolbarTitle: String = ""
    override val toolbarTitleResId: Int? = null
    override val toolbarMenuId: Int? = null
    override val toolbarSubTitle: String = ""
    override val centerToolbarTitle: Boolean = false
    override val centerToolbarSubTitle: Boolean = false

    override fun onMenuItemClick(item: MenuItem?): Boolean = true
    override fun onBackPressed(): Boolean = false
    override fun startActivityForResult(intent: Intent?, requestCode: Int) = Unit
    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) = Unit
    override fun onToolbarBackPressed() = Unit
    override fun showEmptyLayout() = Unit
    override fun onNextPressed() = Unit
}