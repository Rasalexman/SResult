package com.rasalexman.sresultpresentation.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LiveData
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.fragments.IBaseFragment

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

fun <T : SResult<*>> BaseFragment<*>.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.observe(viewLifecycleOwner, {
        stateHandle(it)
    })
}

fun <T : SResult<*>> androidx.lifecycle.LifecycleOwner.onResultChange(
    data: LiveData<T>?,
    stateHandle: InHandler<T>
) {
    data?.observe(this, {
        stateHandle(it)
    })
}

fun <T : Any> BaseFragment<*>.onAnyChange(data: LiveData<T>?, stateHandle: InHandler<T>? = null) {
    data?.observe(viewLifecycleOwner, {
        stateHandle?.invoke(it)
    })
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