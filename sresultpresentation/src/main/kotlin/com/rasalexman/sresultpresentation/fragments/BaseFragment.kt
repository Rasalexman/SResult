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
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.getMessage
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.tools.OnSwipeTouchListener
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel
import java.lang.ref.WeakReference

abstract class BaseFragment<VM : IEventableViewModel> : Fragment(),
    IBaseFragment<VM>, INavigationHandler {

    override val contentView: View?
        get() = this.view

    override var weakContentRef: WeakReference<View>? = null
    override var weakLoadingRef: WeakReference<View>? = null
    override var weakToolbarRef: WeakReference<Toolbar>? = null

    private var onSwipeListener: OnSwipeTouchListener? = null

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
        addViewModelObservers(viewModel)
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
            if(needBackButton) {
                setupBackGesture()
            }
        }
        setupOnBackPressCallback()
    }

    private fun setupBackGesture() {
        onSwipeListener = object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                onBackPressed()
            }
        }
        contentView?.setOnTouchListener(onSwipeListener)
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
        loadingViewLayout?.show()
    }

    /**
     * Hide loading when it's needed
     */
    override fun hideLoading() {
        hideLayoutLoading()
    }

    /**
     * Hide layouts for loading
     */
    protected open fun hideLayoutLoading() {
        hideKeyboard()
        loadingViewLayout?.hide()
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
            showCancel = alert.cancelHandler != null || alert.cancelTitle != null
        )
    }

    /**
     * On Toolbar back button Pressed
     */
    override fun onToolbarBackPressed() {
        navController.popBackStack()
    }

    override fun toolbarTitleHandler(title: String) {
        toolbarView?.setupToolbarTitle(title, toolbarTitleResId, centerToolbarTitle)
    }
    override fun toolbarSubTitleHandler(subtitle: String) {
        toolbarView?.setupToolbarSubtitle(subtitle, centerToolbarTitle)
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
     * Any data types handler for [IBaseViewModel.anyLiveData]
     */
    override fun onAnyDataHandler(data: Any?) = Unit

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
     * Process [SEvent] to view model
     */
    protected open fun processViewEvent(viewEvent: ISEvent) {
        this.viewModel?.processEvent(viewEvent)
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
        contentView?.setOnTouchListener(null)
        onSwipeListener = null
        onBackPressedCallback.isEnabled = false
        clearObservers()
        closeContextAlert()
        clearFragmentView()
        super.onDestroyView()
    }

    protected open fun closeContextAlert() {
        context.closeAlert()
    }

    protected open fun clearFragmentView() {
        if(needToClearView) contentView.clearView()
        //(view as? ViewGroup)?.removeAllViews()
    }

    protected open fun clearObservers() {
        this.clear(this.viewLifecycleOwner)
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
