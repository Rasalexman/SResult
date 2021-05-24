package com.rasalexman.sresultpresentation.extensions

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.rasalexman.sresult.common.extensions.getMessage
import com.rasalexman.sresult.common.typealiases.InHandler
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.fragments.IBaseFragment

private var lastSoftInput = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN

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

fun <T : ISResult<*>> BaseFragment<*>.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.observe(viewLifecycleOwner, {
        stateHandle(it)
    })
}

fun <T : ISResult<*>> androidx.lifecycle.LifecycleOwner.onResultChange(data: LiveData<T>?, stateHandle: InHandler<T>) {
    data?.observe(this, {
        stateHandle(it)
    })
}

fun <T : Any> BaseFragment<*>.onAnyChange(data: LiveData<T>?, stateHandle: InHandler<T>? = null) {
    data?.observe(viewLifecycleOwner, {
        stateHandle?.invoke(it)
    })
}

fun <T : Any> androidx.lifecycle.LifecycleOwner.onAnyChange(data: LiveData<T>?, stateHandle: InHandler<T>? = null) {
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

fun IBaseFragment<*>.showResultError(error: SResult.ErrorResult) {
    hideLoading()
    error.getMessage()?.let {
        if (error is SResult.ErrorResult.Error) {
            showToast(it)
        } else if (error is SResult.ErrorResult.Alert) {
            showAlertDialog(it, error.okTitle, error.okHandler)
        }
    }
}

fun IBaseFragment<*>.initToolbarTitle(toolbarView: Toolbar, titleMarginEnd: Int = 0, titleMarginStart: Int = 0) {
    toolbarView.findViewById<TextView>(R.id.toolbarTitleTextView)?.let { toolbarTitleTextView ->
        toolbarTitleResId?.let {
            toolbarTitleTextView.setText(it)
        } ?: toolbarTitleTextView.setText(toolbarTitle)

        if (centerToolbarTitle) {
            toolbarTitleTextView.gravity = Gravity.CENTER
            if (titleMarginEnd > 0) {
                (toolbarTitleTextView.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    marginEnd = titleMarginEnd
                }
            }
            if (titleMarginStart > 0) {
                (toolbarTitleTextView.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    marginStart = titleMarginStart
                }
            }
        }
    }

    if (toolbarSubTitle.isNotEmpty()) {
        toolbarView.findViewById<TextView>(R.id.toolbarSubTitleTextView)?.apply {
            show()
            text = toolbarSubTitle
            if (centerToolbarSubTitle) {
                gravity = Gravity.CENTER
            }

            if (titleMarginEnd > 0) {
                (layoutParams as? LinearLayout.LayoutParams)?.apply {
                    marginEnd = titleMarginEnd
                }
            }
            if (titleMarginStart > 0) {
                (layoutParams as? LinearLayout.LayoutParams)?.apply {
                    marginStart = titleMarginStart
                }
            }
        }
    }
}