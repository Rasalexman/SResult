package com.rasalexman.sresultpresentation.extensions

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
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
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

private var lastSoftInput = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN

const val KEY_BACK_ARGS = "key_back_arguments"

fun Fragment.hideKeyboard() = activity?.hideKeyboard()
fun Fragment.showKeyboard() {
    this.view?.postDelayed(100L) {
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
    viewModel?.apply {
        resultLiveData?.removeObservers(lifecycleOwner)
        supportLiveData.removeObservers(lifecycleOwner)
        navigationLiveData.removeObservers(lifecycleOwner)
        anyLiveData?.removeObservers(lifecycleOwner)
        liveDataToObserve.forEach { it.removeObservers(lifecycleOwner) }
    }

    weakContentRef?.clear()
    weakLoadingRef?.clear()
    weakToolbarRef?.clear()
    weakContentRef = null
    weakLoadingRef = null
    weakToolbarRef = null
}

fun <T : SResult<*>> Fragment.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.observe(viewLifecycleOwner, { result ->
        result.applyIf(!result.isHandled, stateHandle)
    })
}

fun <T : SResult<*>> androidx.lifecycle.LifecycleOwner.onResultChange(
    data: LiveData<T>?,
    stateHandle: InHandler<T>
) {
    data?.observe(this, { result ->
        result.applyIf(!result.isHandled, stateHandle)
    })
}

fun <T : Any> BaseFragment<*>.onAnyChange(data: LiveData<T>?, stateHandle: InHandler<T>? = null) {
    data?.observe(viewLifecycleOwner, {
        stateHandle?.invoke(it)
    })
}

fun <B : ViewDataBinding, VM : BaseViewModel> IBaseBindingFragment<B, VM>.setupBinding(
    inflater: LayoutInflater,
    container: ViewGroup?
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
        inflater.createBinding<B>(
            layoutId = layoutId,
            container = container,
            attachToParent = false,
            findLifeCycle = true
        )
    }.also {
        it.executePendingBindings()
        currentBinding = it
        initBinding(it)
    }.root
}

fun <T : Any> androidx.lifecycle.LifecycleOwner.onAnyChange(
    data: LiveData<T>?,
    stateHandle: InHandler<T>? = null
) {
    data?.observe(this, {
        stateHandle?.invoke(it)
    })
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
            toolbarView.setNavigationIconColor(it)
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

inline val Fragment.windowHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = requireActivity().windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.height() - insets.bottom - insets.top
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

inline val Fragment.windowWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = requireActivity().windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }