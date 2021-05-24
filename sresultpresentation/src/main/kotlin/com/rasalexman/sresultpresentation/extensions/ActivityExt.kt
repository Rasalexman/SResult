package com.rasalexman.sresultpresentation.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun Activity.hideKeyboard() {
    val v = currentFocus ?: View(this)
    if (v is EditText) v.clearFocus()
    v.windowToken?.let {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
            it,
            0
        )
    }
}

fun Activity.showKeyboardIfNeeded() {
    currentFocus?.windowToken?.let {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
            currentFocus,
            InputMethodManager.SHOW_IMPLICIT
        )
    }
}