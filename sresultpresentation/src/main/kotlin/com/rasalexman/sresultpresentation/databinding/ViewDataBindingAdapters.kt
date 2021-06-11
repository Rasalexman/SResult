package com.rasalexman.sresultpresentation.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.rasalexman.sresultpresentation.extensions.*

@BindingAdapter("isGone")
fun setIsGone(view: View, isGone: Boolean?) {
    view.apply {
        if(isGone == true) {
            hide()
        } else {
            show()
        }
    }
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean?) {
    view.apply {
        if(visible == true) {
            show()
        } else {
            hide()
        }
    }
}

@BindingAdapter("invisible")
fun setInvisible(view: View, invisible: Boolean?) {
    view.apply {
        if(invisible == true) {
            hide(false)
        } else {
            show()
        }
    }
}

@BindingAdapter("selected")
fun setSelected(view: View, selected: Boolean?) {
    view.isSelected = selected ?: false
}

@BindingAdapter("requestFocus")
fun requestFocus(view: View, @Suppress("UNUSED_PARAMETER") requestFocus: Any?) {
    requestFocus?.let {
        view.requestFocus()
    }
}

@BindingAdapter("android:enabled")
fun setEnabled(view: View, enabled: Boolean?) {
    enabled?.let {
        view.isEnabled = it
        view.isClickable = it
    }
}

@BindingAdapter("onViewClick")
fun setOnClickListener(view: View, listener: View.OnClickListener?) {
    view.setOnDebounceClickListener {
        listener?.onClick(view)
    }
}

@BindingAdapter(value = ["onFocus", "onFocusAttChanged"], requireAll = false)
fun setViewFocusChanged(view: View, onFocus: Boolean?, focusListener: InverseBindingListener?) {
    onFocus?.let {
        view.setOnFocusChangeListener { _, _ ->
            focusListener?.onChange()
        }
    }
}

@InverseBindingAdapter(attribute = "onFocus", event = "onFocusAttChanged")
fun getViewFocusChanged(view: View): Boolean {
    return view.hasFocus()
}