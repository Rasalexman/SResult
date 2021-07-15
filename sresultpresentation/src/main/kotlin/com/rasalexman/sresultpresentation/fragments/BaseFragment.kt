package com.rasalexman.sresultpresentation.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.getMessage
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultpresentation.extensions.AnyResultLiveData
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import java.lang.ref.WeakReference


abstract class BaseFragment<VM : IBaseViewModel> : Fragment(), IBaseFragment<VM>,
    INavigationHandler {

    override val contentView: View?
        get() = this.view

    /**
     * Need try again error button click
     */
    open val needTryAgainButton: Boolean = false

    /**
     * Toolbar title
     */
    override val toolbarTitle: String? = null

    /**
     * Toolbar subtitle
     */
    override val toolbarSubTitle: String? = null


    /**
     * Toolbar title resourceId
     */
    override val toolbarTitleResId: Int? = null

    override var weakContentRef: WeakReference<View>? = null
    override var weakLoadingRef: WeakReference<View>? = null
    override var weakToolbarRef: WeakReference<Toolbar>? = null

    /**
     * Toolbar menu resId
     */
    @MenuRes
    override val toolbarMenuId: Int? = null

    /**
     * Fragment ViewModel instance
     */
    override val viewModel: VM? = null

    /**
     * Fragment default input mode
     */
    open val fragmentInputMode: Int? = null

    /**
     * Activity original input mode
     */
    private var activityOriginalInputMode: Int? = null

    /**
     * Current [INavigationHandler] for check in Main activity class
     */
    override val currentNavHandler: INavigationHandler?
        get() = this

    /**
     * Current Navigation Controller
     */
    override val navController: NavController
        get() = this.findNavController()

    protected open val onBackPressedCallback by unsafeLazy {
        object : OnBackPressedCallback(canPressBack) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    /**
     * when need to create view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBackStackArguments()
        showToolbar()
        initLayout()

        viewModel?.let { currentBaseVm ->
            addResultLiveDataObservers(currentBaseVm)
            addSupportLiveDataObservers(currentBaseVm)
            addAnyLiveDataObservers(currentBaseVm)
            addNavigateLiveDataObserver(currentBaseVm)
            addToolbarLiveDataObserver(currentBaseVm)

            currentBaseVm.liveDataToObserve.forEach {
                onAnyChange(it)
            }
        }
    }

    protected open fun observeBackStackArguments() {
        val navController = findNavController()
        navController.currentBackStackEntry?.apply {
            savedStateHandle.getLiveData<Bundle>(KEY_BACK_ARGS)
                .observe(viewLifecycleOwner, ::onBackArgumentsHandler)
        }
    }
    // Handler for back navigation result data
    protected open fun onBackArgumentsHandler(backArgs: Bundle) = Unit

    protected open fun addToolbarLiveDataObserver(currentViewMode: IBaseViewModel) {
        currentViewMode.toolbarTitle?.observe(viewLifecycleOwner) {
            toolbarView?.setupToolbarTitle(it, toolbarTitleResId, centerToolbarTitle)
        }
        currentViewMode.toolbarSubTitle?.observe(viewLifecycleOwner) {
            toolbarView?.setupToolbarSubtitle(it, centerToolbarTitle)
        }
    }

    protected open fun addNavigateLiveDataObserver(currentViewMode: IBaseViewModel) {
        currentViewMode.navigationLiveData.apply(::observeNavigationLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    protected open fun addAnyLiveDataObservers(currentViewMode: IBaseViewModel) {
        currentViewMode.anyLiveData?.apply(::observeAnyLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun addResultLiveDataObservers(currentViewMode: IBaseViewModel) {
        (currentViewMode.resultLiveData as? AnyResultLiveData)?.let {
            onResultChange(it, ::onResultHandler)
        }
    }

    /**
     * Add Observer for Error Live Data handles (ex. from CoroutinesManager)
     */
    protected open fun addSupportLiveDataObservers(currentViewMode: IBaseViewModel) {
        onResultChange(currentViewMode.supportLiveData, ::onResultHandler)
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

            if (needBackButton) {
                toolbarBackButtonResId?.let {
                    toolbar.setNavigationIcon(it)
                }
                toolbar.setNavigationOnClickListener {
                    onToolbarBackPressed()
                }
            }
        }
        setupOnBackPressCallback()
    }

    protected open fun setupOnBackPressCallback() {
        if (canPressBack) {
            onBackPressedCallback.isEnabled = true
            this.requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, onBackPressedCallback)
        } else {
            onBackPressedCallback.isEnabled = false
        }
    }

    /**
     * When need to inflate menu
     */
    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) {
        toolbar.apply {
            menu?.clear()
            inflateMenu(menuResId)
        }.setOnMenuItemClickListener(this)
    }

    /**
     * On click to menu item handler
     */
    override fun onMenuItemClick(item: MenuItem): Boolean {
        return true
    }

    /**
     * Support function to implement to set view Listeners (ex. button.setOnClickListener)
     * starts in onViewCreated()
     */
    open fun initLayout() = Unit


    override fun onResume() {
        super.onResume()
        applyInputMode()
    }

    override fun onPause() {
        cancelInputMode()
        super.onPause()
    }

    protected open fun applyInputMode() {
        fragmentInputMode?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                activityOriginalInputMode = activity?.window?.attributes?.softInputMode
                activity?.window?.setSoftInputMode(it)
            }, SAVE_INPUT_MODE_DELAY_MS)
        }
    }

    protected open fun cancelInputMode() {
        activityOriginalInputMode?.let { activity?.window?.setSoftInputMode(it) }
    }

    /**
     * When [SResult.Success] is coming from any observer
     */
    override fun showSuccess(result: SResult.Success<*>) = Unit

    /**
     * Show loading state for [SResult.Loading]
     */
    override fun showLoading() {
        showLayoutLoading()
    }

    /**
     * Show layouts for loading
     */
    protected open fun showLayoutLoading() {
        hideKeyboard()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    /**
     * Hide loading when it's needed
     */
    override fun hideLoading() {
        hideLayoutLoading()
    }

    /**
     * Hide laouts for loading
     */
    protected open fun hideLayoutLoading() {
        hideKeyboard()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }

    /**
     * Show toast message for [SResult.AbstractFailure.Error]
     */
    override fun showToast(message: Any?, interval: Int) {
        hideKeyboard()
        hideLoading()
        toast(message, interval)
    }

    /**
     * Show toast error from error result data
     */
    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        this.toast(error.getMessage(), error.interval)
    }

    /**
     * Show simple alert dialog from result data
     */
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

    /**
     * On Toolbar back button Pressed
     */
    override fun onToolbarBackPressed() {
        navController.popBackStack()
    }

    /**
     * When pressed back
     */
    override fun onBackPressed(): Boolean {
        return if (!canPressBack) true else {
            activity?.hideKeyboard()
            navController.popBackStack()
        }
    }

    /**
     * When clicked on next screen or emit text result action [SResult.NavigateResult.NavigateNext]
     */
    override fun onNextPressed() = Unit

    /**
     * Show [SResult.Progress] state with data
     */
    override fun showProgress(progress: Int, message: Any?) = Unit

    /**
     * For support navigation handle
     */
    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    /**
     * Base [SResult] handle function
     */
    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    /**
     * Any data types handler for [IBaseViewModel.anyLiveData]
     */
    protected open fun onAnyDataHandler(data: Any?) = Unit

    /**
     * Observe Any type of [LiveData] with callback
     */
    protected open fun <T : Any> observeAnyLiveData(data: LiveData<T>, callback: InHandler<T>) {
        onAnyChange(data, callback)
    }

    /**
     * Observe Any type of [LiveData] with callback
     */
    protected open fun observeAnyLiveData(data: LiveData<*>) {
        onAnyChange(data, ::onAnyDataHandler)
    }

    /**
     * Observe only [SResult.NavigateResult] live data
     */
    protected open fun observeNavigationLiveData(data: LiveData<SResult.NavigateResult>) {
        onResultChange(data, ::onResultHandler)
    }

    /**
     * Process [SEvent] to view model
     */
    protected open fun processViewEvent(viewEvent: ISEvent) {
        this.viewModel?.processViewEvent(viewEvent)
    }

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

    /**
     * When navigation is broke
     */
    override fun showNavigationError(e: Exception?, navResId: Int?) {
        loggE(
            e,
            "There is no navigation direction from ${this::class.java.simpleName} with contentViewLayout id = $navResId"
        )
        showToast(R.string.error_internal, Toast.LENGTH_LONG)
    }

    /**
     * Show empty layout on the [SResult.Empty]
     */
    override fun showEmptyLayout() {
        hideLoading()
    }

    /**
     * When view destroy
     */
    override fun onDestroyView() {
        onBackPressedCallback.isEnabled = false
        this.clear(this.viewLifecycleOwner)
        context.closeAlert()
        view.clearView()
        super.onDestroyView()
    }

    /**
     * Проверка, есть ли запрошенное разрешение
     * @param permission необходимое разрешение
     */
    protected fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val SAVE_INPUT_MODE_DELAY_MS = 100L
    }
}
