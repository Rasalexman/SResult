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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResultLiveData
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel


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
     * Back button resourceId
     */
    protected open val toolbarBackButtonResId: Int
        get() = R.drawable.ic_arrow_back_black_24dp

    /**
     * Need to center toolbar title
     */
    override val centerToolbarTitle: Boolean = false

    /**
     * Need to center toolbar subtitle
     */
    override val centerToolbarSubTitle: Boolean = false

    /**
     * Toolbar menu resId
     */
    @MenuRes
    override val toolbarMenuId: Int? = null

    /**
     * Can this fragment navigate back or can be pressed back button
     */
    open val canPressBack: Boolean = true

    /**
     * Fragment ViewModel instance
     */
    override val viewModel: VM? = null

    /**
     * Does this fragment need toolbar back button
     */
    protected open val needBackButton: Boolean = false

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

        showToolbar()
        initLayout()

        addResultLiveDataObservers()
        addSupportLiveDataObservers()
        addAnyLiveDataObservers()
        addNavigateLiveDataObserver()

        viewModel?.liveDataToObserve?.forEach {
            onAnyChange(it)
        }
    }

    protected open fun addNavigateLiveDataObserver() {
        viewModel?.navigationLiveData?.apply(::observeNavigationLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    protected open fun addAnyLiveDataObservers() {
        viewModel?.anyLiveData?.apply(::observeAnyLiveData)
    }

    /**
     * Add Standard Live data Observers to handler [SResult] event
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun addResultLiveDataObservers() {
        (viewModel?.resultLiveData as? AnyResultLiveData)?.apply(::observeResultLiveData)
    }

    /**
     * Add Observer for Error Live Data handles (ex. from CoroutinesManager)
     */
    protected open fun addSupportLiveDataObservers() {
        viewModel?.supportLiveData?.apply(::observeResultLiveData)
    }

    /**
     * For Add Custom Toolbar instance as SupportActionBar
     */
    protected open fun showToolbar() {
        toolbarView?.let { toolbar ->

            toolbarMenuId?.let {
                inflateToolBarMenu(toolbar, it)
            }

            val titleMarginEnd = dimen(if (needBackButton) R.dimen.size_48dp else R.dimen.size_16dp)
            initToolbarTitle(toolbar, titleMarginEnd)

            if (needBackButton) {
                toolbar.setNavigationIcon(toolbarBackButtonResId)
                toolbar.setNavigationOnClickListener {
                    onToolbarBackPressed()
                }
            }
        }

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
        toolbar.menu?.clear()
        toolbar.inflateMenu(menuResId)
        toolbar.setOnMenuItemClickListener(this)
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

    /**
     * Show alert dialog for [SResult.ErrorResult.Alert]
     */
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
     * Show toast message for [SResult.ErrorResult.Error]
     */
    override fun showToast(message: Any, interval: Int) {
        hideKeyboard()
        hideLoading()
        toast(message, interval)
    }

    override fun onResume() {
        super.onResume()
        fragmentInputMode?.let {

            Handler(Looper.getMainLooper()).postDelayed({
                activityOriginalInputMode = activity?.window?.attributes?.softInputMode
                activity?.window?.setSoftInputMode(it)
            }, SAVE_INPUT_MODE_DELAY_MS)
        }

    }

    override fun onPause() {
        activityOriginalInputMode?.let { activity?.window?.setSoftInputMode(it) }
        super.onPause()
    }

    /**
     * Show loading state for [SResult.Loading]
     */
    override fun showLoading() {
        showLayoutLoading()
    }

    protected fun showLayoutLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    /**
     * Hide loading when it's needed
     */
    override fun hideLoading() {
        hideLayoutLoading()
    }

    protected fun hideLayoutLoading() {
        hideKeyboard()
        errorViewLayout?.hide()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }

    /**
     * Show error result handler
     */
    override fun showError(error: SResult.ErrorResult) {
        this.showResultError(error)
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
     * For support navigation handle
     */
    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    /**
     * When view destroy
     */
    override fun onDestroyView() {
        onBackPressedCallback.isEnabled = false
        context.closeAlert()
        view.clearView()
        (view as? ViewGroup)?.removeAllViews()
        viewModel?.liveDataToObserve?.forEach { it.removeObservers(this.viewLifecycleOwner) }
        super.onDestroyView()
    }

    /**
     * Base [SResult] handle function
     */
    override fun onResultHandler(result: ISResult<*>) {
        onBaseResultHandler(result)
    }

    /**
     *
     */
    protected open fun onAnyDataHandler(data: Any?) = Unit

    /**
     * Observe Any type of [LiveData] with callback
     */
    protected open fun <T : Any> observeAnyLiveData(data: LiveData<T>, callback: InHandler<T>) {
        onAnyChange(data, callback)
    }

    /**
     *
     */
    protected open fun observeAnyLiveData(data: LiveData<*>) {
        onAnyChange(data, ::onAnyDataHandler)
    }

    /**
     * Observe only [SResult] live data
     */
    protected open fun observeResultLiveData(data: LiveData<SResult<*>>) {
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
     * Process [SEvent] to view model
     */
    protected open fun processViewEvent(viewEvent: ISEvent) {
        this.viewModel?.processViewEvent(viewEvent)
    }

    /**
     * Navigate to direction
     */
    override fun navigateTo(direction: NavDirections) {
        try {
            navController.navigate(direction)
        } catch (e: Exception) {
            loggE(e, "There is no navigation direction with id = ${direction.actionId}")
            Navigation.findNavController(
                requireActivity(),
                R.id.mainHostFragment
            ).navigate(direction)
        }
    }

    /**
     * Navigate by navResId
     */
    override fun navigateBy(navResId: Int) {
        navResId.let(navController::navigate)
    }

    /**
     * Navigate back by pop with navResId
     */
    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean) {
        navController.apply {
            navResId?.let {
                popBackStack(it, isInclusive)
            } ?: popBackStack()
        }
    }

    /**
     * Show empty layout on the [SResult.Empty]
     */
    override fun showEmptyLayout() {
        hideLoading()
    }

    /**
     * Show error layout with needed resources
     *
     * @param imageResId - drawable image
     * @param textResId - string text
     */
    override fun showErrorLayout(
        @DrawableRes imageResId: Int?,
        @StringRes textResId: Int?,
        @StringRes buttonTitleResId: Int?,
        tryAgainHandler: UnitHandler?
    ) {
        errorViewLayout?.apply {
            loadingViewLayout?.hide()
            contentViewLayout?.hide()
            show()
            findViewById<ImageView>(R.id.errorImageView)?.apply {
                imageResId?.let {
                    this.show()
                    this.setImageResource(imageResId)
                } ?: this.hide()
            }
            findViewById<TextView>(R.id.errorTextView)?.apply {
                textResId?.let {
                    this.show()
                    this.text = string(textResId)
                } ?: this.hide()
            }
            if (needTryAgainButton || buttonTitleResId != null) {
                findViewById<Button>(R.id.errorActionButton)?.apply {
                    buttonTitleResId?.let(this::setText)
                    show()
                    setOnClickListener {
                        tryAgainHandler?.invoke() ?: processViewEvent(SEvent.TryAgain)
                    }
                }
            } else {
                findViewById<Button>(R.id.errorActionButton)?.hide()
            }
        }
    }

    /**
     * Проверка, есть ли запрошенное разрешение
     * @param permission необходимое разрешение
     */
    protected fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val SAVE_INPUT_MODE_DELAY_MS = 100L
    }
}
