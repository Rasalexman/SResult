package com.rasalexman.sresultpresentation.databinding

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.rasalexman.sresultpresentation.extensions.setInvisible
import com.rasalexman.sresultpresentation.extensions.setOnDebounceClickListener
import com.rasalexman.sresultpresentation.extensions.setVisible

@BindingAdapter("isGone")
fun setIsGone(view: View, isGone: Boolean?) {
    view.setVisible(!(isGone ?: false))
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean?) {
    view.setVisible(visible ?: false)
}

@BindingAdapter("invisible")
fun setInvisible(view: View, invisible: Boolean?) {
    view.setInvisible(invisible ?: false)
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