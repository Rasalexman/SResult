@file:Suppress("unused")
package com.rasalexman.sresultpresentation.extensions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.postDelayed
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.rasalexman.easyrecyclerbinding.createBinding
import com.rasalexman.easyrecyclerbinding.createBindingWithViewModel
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.databinding.IBaseBindingFragment
import com.rasalexman.sresultpresentation.dialogs.BaseDialogFragment
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.layout.BaseLayout
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel
import com.rasalexman.sresultpresentation.viewModels.ISelectDateViewModel
import com.rasalexman.sresultpresentation.viewModels.flowable.IFlowableViewModel
import java.util.*

private var lastSoftInput = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN

const val KEY_BACK_ARGS = "key_back_arguments"

fun Fragment.hideKeyboard() = activity?.hideKeyboard()
fun Fragment.showKeyboard(delay: Long = 100L) {
    this.view?.postDelayed(delay) {
        activity?.showKeyboardIfNeeded()
    }
}

//the same for Fragments
fun Fragment.dip(value: Int): Int = requireActivity().dip(value)
fun Fragment.dip(value: Float): Int = requireActivity().dip(value)
fun Fragment.sp(value: Int): Int = requireActivity().sp(value)
fun Fragment.sp(value: Float): Int = requireActivity().sp(value)
fun Fragment.px2dip(px: Int): Float = requireActivity().px2dip(px)
fun Fragment.px2sp(px: Int): Float = requireActivity().px2sp(px)
fun Fragment.dimen(@DimenRes resource: Int): Int = requireActivity().dimen(resource)
fun Fragment.color(@ColorRes resource: Int): Int = requireActivity().color(resource)
fun Fragment.string(@StringRes resource: Int): String = requireContext().string(resource)
fun Fragment.stringArr(@ArrayRes resource: Int): Array<String> = resources.getStringArray(resource)
fun Fragment.drawable(@DrawableRes resource: Int): Drawable? = requireActivity().drawable(resource)

fun IBaseFragment<*>.clear(lifecycleOwner: LifecycleOwner) {
    viewModel?.clearViewModel(lifecycleOwner)
}

fun IBaseFragment<*>.clearOnViewDestroy(lifecycleOwner: LifecycleOwner) {
    viewModel?.clearOnViewDestroyViewModel(lifecycleOwner)
    weakContentRef?.clear()
    weakLoadingRef?.clear()
    weakToolbarRef?.clear()
    weakContentRef = null
    weakLoadingRef = null
    weakToolbarRef = null
}

fun IEventableViewModel.clearViewModel(lifecycleOwner: LifecycleOwner) {
    when(this) {
        is IBaseViewModel -> this.apply {
            resultLiveData?.removeObservers(lifecycleOwner)
            supportLiveData.removeObservers(lifecycleOwner)
            navigationLiveData.removeObservers(lifecycleOwner)
            eventLiveData.removeObservers(lifecycleOwner)
            anyLiveData?.removeObservers(lifecycleOwner)
        }
        is IFlowableViewModel -> this.clear()
    }
}

private fun IEventableViewModel.clearOnViewDestroyViewModel(lifecycleOwner: LifecycleOwner) {
    when(this) {
        is IBaseViewModel -> this.apply {
            toolbarTitle?.removeObservers(lifecycleOwner)
            toolbarSubTitle?.removeObservers(lifecycleOwner)
            toolbarMenu?.removeObservers(lifecycleOwner)
            liveDataToObserve.forEach { it.removeObservers(lifecycleOwner) }
            eventLiveData.postValue(null)
        }
        is IFlowableViewModel -> this.clear()
    }
}

/**
 * Add Data observers to IBaseFragment viewModel
 */
fun IBaseFragment<*>.addOnCreateViewModelObservers(vm: IEventableViewModel?) {
    when(vm) {
        is IBaseViewModel -> observeBaseViewModel(vm)
        is IFlowableViewModel -> observeStateViewModel(vm)
    }
}

fun IBaseFragment<*>.addOnViewCreatedViewModelObservers(vm: IEventableViewModel?) {
    when(vm) {
        is IBaseViewModel -> observeLifecycleBaseViewModel(vm)
        is IFlowableViewModel -> observeLifecycleStateViewModel(vm)
    }
}

@Suppress("UNCHECKED_CAST")
private fun IBaseFragment<*>.observeBaseViewModel(currentBaseVm: IBaseViewModel) {
    val lifecycleOwner = when(this) {
        is BaseFragment<*> -> this
        is BaseDialogFragment<*> -> this
        is BaseLayout<*> -> this
        else -> null
    }
    lifecycleOwner?.apply {
        onResultChange(currentBaseVm.navigationLiveData, ::onResultHandler)
        onResultChange(currentBaseVm.supportLiveData, ::onResultHandler)
        onResultChange((currentBaseVm.resultLiveData as? AnyResultLiveData), ::onResultHandler)
        onAnyChange(currentBaseVm.anyLiveData, ::onAnyDataHandler)
        onAnyChange(currentBaseVm.eventLiveData)
    }
}

private fun IBaseFragment<*>.observeLifecycleBaseViewModel(currentBaseVm: IBaseViewModel) {
    val lifecycleOwner = when(this) {
        is BaseFragment<*> -> this.viewLifecycleOwner
        is BaseDialogFragment<*> -> this.viewLifecycleOwner
        is BaseLayout<*> -> this
        else -> null
    }
    lifecycleOwner?.apply {
        onAnyChange(currentBaseVm.toolbarTitle, ::toolbarTitleHandler)
        onAnyChange(currentBaseVm.toolbarSubTitle, ::toolbarSubTitleHandler)
        onAnyChange(currentBaseVm.toolbarMenu, ::toolbarMenuHandler)

        currentBaseVm.liveDataToObserve.forEach {
            onAnyChange(it)
        }
    }
}

private fun IBaseFragment<*>.observeLifecycleStateViewModel(currentFlowableVM: IFlowableViewModel) {
    val lifecycleOwner = when(this) {
        is BaseFragment<*> -> this.viewLifecycleOwner
        is BaseDialogFragment<*> -> this.viewLifecycleOwner
        is BaseLayout<*> -> this
        else -> null
    }
    lifecycleOwner?.lifecycleScope?.let { vmScope ->
        currentFlowableVM.toolbarTitle?.let {
            vmScope.launchWhenStarted {
                it.collect {
                    toolbarTitleHandler(it)
                }
            }
        }

        currentFlowableVM.toolbarSubTitle?.let {
            vmScope.launchWhenStarted {
                it.collect {
                    toolbarSubTitleHandler(it)
                }
            }
        }
    }
}

private fun IBaseFragment<*>.observeStateViewModel(currentFlowableVM: IFlowableViewModel) {
    val lifecycleOwner = when(this) {
        is BaseFragment<*> -> this
        is BaseDialogFragment<*> -> this
        is BaseLayout<*> -> this
        else -> null
    }

    lifecycleOwner?.lifecycleScope?.let { vmScope ->

        currentFlowableVM.resultFlow?.let {
            vmScope.launchWhenCreated {
                it.collect { result ->
                    if(result is SResult<*>) {
                        result.handleResult(::onResultHandler)
                    } else {
                        onAnyDataHandler(result)
                    }
                }
            }
        }

        vmScope.launchWhenCreated {
            currentFlowableVM.navigationFlow.collect { result ->
                result.handleResult(::onResultHandler)
            }
        }

        vmScope.launchWhenCreated {
            currentFlowableVM.supportFlow.collect { result ->
                result.handleResult(::onResultHandler)
            }
        }

        currentFlowableVM.anyDataFlow?.let {
            vmScope.launchWhenCreated {
                it.collect { onAnyDataHandler(it) }
            }
        }
    }
}


fun <T : SResult<*>> Fragment.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.let { currentLiveData ->
        if(!currentLiveData.hasObservers()) {
            currentLiveData.observe(this) { result ->
                result.handleResult(stateHandle)
            }
        }
    }
}

fun <T : SResult<*>> DialogFragment.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.let { currentLiveData ->
        if(!currentLiveData.hasObservers()) {
            currentLiveData.observe(this) { result ->
                result.handleResult(stateHandle)
            }
        }
    }
}

fun <T : SResult<*>> LifecycleOwner.onResultChange(
    data: LiveData<T>?,
    stateHandle: InHandler<T>
) {
    data?.let { currentLiveData ->
        if(!currentLiveData.hasObservers()) {
            currentLiveData.observe(this) { result ->
                result.handleResult(stateHandle)
            }
        }
    }
}

fun <T : Any> BaseFragment<*>.onAnyChange(data: LiveData<T>?, stateHandle: InHandler<T>? = null) {
    data?.observe(viewLifecycleOwner) {
        stateHandle?.invoke(it)
    }
}

/**
 * Handle SResult optionally
 */
private fun<T : SResult<*>> T?.handleResult(stateHandle: InHandler<T>) {
    this?.let { currentResult ->
        val isAlreadyHandled = !currentResult.isHandled
        currentResult.applyIf(isAlreadyHandled, stateHandle)
    }
}

/**
 * Create DataBinding with or without view model
 */
fun <B : ViewDataBinding, VM : BaseContextViewModel> IBaseBindingFragment<B, VM>.setupBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    needPending: Boolean = true
): View {
    return viewModel?.let { vm ->
        (this as Fragment).createBindingWithViewModel<B, VM>(
            layoutId = layoutId,
            viewModel = vm,
            viewModelBRId = BR.vm,
            container = container,
            attachToParent = false
        )
    }.or {
        inflater.createBinding(
            layoutId = layoutId,
            container = container,
            attachToParent = false,
            findLifeCycle = true
        )
    }.also {
        currentBinding = it
        initBinding(it)
        if(needPending) {
            it.executePendingBindings()
        }
    }.root
}

/**
 * Handler for any data change liveData
 */
fun <T : Any> LifecycleOwner.onAnyChange(
    data: LiveData<T>?,
    stateHandle: InHandler<T>? = null
) {
    data?.let { liveData ->
        if(!liveData.hasObservers()) {
            liveData.observe(this) {
                stateHandle?.invoke(it)
            }
        }
    }
}

fun Fragment.setSoftInputMode(mode: Int = -1) {
    this.activity?.let {
        if (mode >= 0) {
            lastSoftInput = it.window?.attributes?.softInputMode ?: lastSoftInput
            it.window?.setSoftInputMode(mode)
        } else {
            it.window?.setSoftInputMode(lastSoftInput)
        }
    }
}

fun IBaseFragment<*>.initToolbarTitle(
    toolbarView: Toolbar
) {
    toolbarView.setupToolbarTitle(
        toolbarTitle,
        toolbarTitleResId,
        centerToolbarTitle
    )

    toolbarView.setupToolbarSubtitle(
        toolbarSubTitle,
        centerToolbarSubTitle
    )
}

fun IBaseFragment<*>.initToolbarNavigationIcon(
    toolbarView: Toolbar
) {
    if (needBackButton) {
        toolbarBackButtonResId?.let {
            toolbarView.setNavigationIcon(it)
        }
        toolbarNavigationIconColor?.let {
           if(Build.VERSION.SDK_INT >= 21) {
               toolbarView.setNavigationIconColor(it)
           }
        }
        toolbarView.setNavigationOnClickListener {
            onToolbarBackPressed()
        }
    }
}

fun Toolbar.setupToolbarSubtitle(
    toolbarSubTitle: String?,
    centerToolbarSubTitle: Boolean
) {
    this.subtitle = null
    this.findViewById<TextView>(R.id.toolbarSubTitleTextView)?.apply {
        if (!toolbarSubTitle.isNullOrEmpty()) {
            show()
            text = toolbarSubTitle
            if (centerToolbarSubTitle) {
                gravity = Gravity.CENTER
            }
        } else {
            hide()
        }
    }.or {
        this.subtitle = toolbarSubTitle
    }
}

fun Toolbar.setupToolbarTitle(
    toolbarTitle: String?,
    toolbarTitleResId: Int?,
    centerToolbarTitle: Boolean
) {
    this.findViewById<TextView>(R.id.toolbarTitleTextView)?.let { toolbarTitleTextView ->
        toolbarTitle?.let(toolbarTitleTextView::setText).or {
            toolbarTitleResId?.let(toolbarTitleTextView::setText)
        }

        if (centerToolbarTitle) {
            toolbarTitleTextView.gravity = Gravity.CENTER
        }
    }.or {
        toolbarTitleResId?.let {
            this.setTitle(it)
        }.or { this.title = toolbarTitle }
    }
}

fun IBaseFragment<*>.navigatePopTo(
    context: Context?,
    navigator: NavController?,
    navResId: Int?,
    isInclusive: Boolean,
    backArgs: Bundle?
) {
    if (navigator != null) {
        try {
            navigator.apply {
                backArgs?.let {
                    previousBackStackEntry?.savedStateHandle?.set(KEY_BACK_ARGS, backArgs)
                }
                navResId?.let {
                    popBackStack(it, isInclusive)
                } ?: popBackStack()
            }
        } catch (e: Exception) {
            loggE(
                e,
                "There is no navigation direction from ${this::class.java.simpleName} with id = $navResId"
            )
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        mainHostFragmentId
                    ).apply {
                        backArgs?.let {
                            previousBackStackEntry?.savedStateHandle?.set(KEY_BACK_ARGS, backArgs)
                        }
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
}

fun IBaseFragment<*>.navigatePop(context: Context?, args: Bundle? = null) {
    try {
        (context as? FragmentActivity)?.let {
            Navigation.findNavController(
                it,
                mainHostFragmentId
            ).apply {
                args?.let { backArgs ->
                    previousBackStackEntry?.savedStateHandle?.set(KEY_BACK_ARGS, backArgs)
                }
                popBackStack()
            }
        } ?: showNavigationError(NullPointerException("context is not FragmentActivity"), null)
    } catch (e: Exception) {
        showNavigationError(e, null)
    }
}

fun IBaseFragment<*>.navigateBy(context: Context?, navigator: NavController?, navResId: Int?) {
    if (navResId != null && navigator != null) {
        try {
            navigator.navigate(navResId)
        } catch (e: Exception) {
            loggE(
                e,
                "There is no navigation direction from ${this::class.java.simpleName} with id = $navResId"
            )
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        mainHostFragmentId
                    ).navigate(navResId)
                } ?: showNavigationError(null, navResId)
            } catch (e: Exception) {
                showNavigationError(e, navResId)
            }
        }
    }
}

fun IBaseFragment<*>.navigateTo(
    context: Context?,
    navigator: NavController?,
    direction: NavDirections?
) {
    if (direction != null && navigator != null) {
        try {
            navigator.navigate(direction)
        } catch (e: Exception) {
            loggE(
                e,
                "There is no navigation direction from ${this::class.java.simpleName} with id = ${direction.actionId}"
            )
            try {
                (context as? FragmentActivity)?.let {
                    Navigation.findNavController(
                        it,
                        mainHostFragmentId
                    ).navigate(direction)
                } ?: showNavigationError(null, direction.actionId)
            } catch (e: Exception) {
                showNavigationError(e, direction.actionId)
            }
        }
    }
}

fun <VM : ISelectDateViewModel> IBaseFragment<VM>.showDateTimePickDialog(
    type: DateType = DateType.NONE,
    dialogTitle: String = ""
) {
    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    (this as? Fragment)?.let {
        val fragmentContext = context
        if (fragmentContext != null) {
            val datePickerDialog = DatePickerDialog(fragmentContext, { _, year, month, day ->
                TimePickerDialog(fragmentContext, { _, hour, minute ->
                    val pickedDateTime = Calendar.getInstance(Locale.getDefault())
                    pickedDateTime.set(year, month, day, hour, minute)
                    viewModel?.onDateSelected(pickedDateTime.timeInMillis)
                }, startHour, startMinute, true).show()
            }, startYear, startMonth, startDay)

            datePickerDialog.apply {
                if (dialogTitle.isNotEmpty()) {
                    setTitle(dialogTitle)
                }
                when (type) {
                    DateType.MAX_DATE -> datePicker.maxDate = Date().time
                    DateType.MIN_DATE -> datePicker.minDate = Date().time
                    DateType.NONE -> Unit
                }
            }
            datePickerDialog.show()
        }
    }
}

enum class DateType {
    MAX_DATE, MIN_DATE, NONE
}

@Suppress("DEPRECATION")
inline val Fragment.windowHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = requireActivity().windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.height() - insets.bottom - insets.top
        } else {
            val displayMetrics = DisplayMetrics()
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            } else {
                displayMetrics.setToDefaults()
            }
            displayMetrics.heightPixels
        }
    }

@Suppress("DEPRECATION")
inline val Fragment.windowWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = requireActivity().windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            } else {
                displayMetrics.setToDefaults()
            }
            displayMetrics.widthPixels
        }
    }