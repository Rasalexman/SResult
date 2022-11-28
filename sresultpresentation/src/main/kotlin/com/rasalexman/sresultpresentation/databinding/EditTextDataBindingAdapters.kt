package com.rasalexman.sresultpresentation.databinding

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.rasalexman.sresultpresentation.extensions.hideKeyboard

@BindingAdapter(value = ["onOkInSoftKeyboard"])
fun setOnOkInSoftKeyboardListener(editText: EditText, onOkInSoftKeyboardListener: OnOkInSoftKeyboardListener?) {
    if (onOkInSoftKeyboardListener == null) {
        editText.setOnEditorActionListener(null)
        return
    }
    editText.setOnEditorActionListener(null)
    editText.setOnEditorActionListener { view: TextView?, actionId: Int?, event: KeyEvent? ->
        view?.hideKeyboard()
        onOkInSoftKeyboardListener.onOkInSoftKeyboard(actionId, event)
    }
}

interface OnOkInSoftKeyboardListener {
    fun onOkInSoftKeyboard(actionId: Int?, event: KeyEvent?): Boolean
}