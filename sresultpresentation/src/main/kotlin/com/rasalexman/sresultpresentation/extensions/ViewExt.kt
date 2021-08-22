package com.rasalexman.sresultpresentation.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.rasalexman.easyrecyclerbinding.changeCallbackMap
import com.rasalexman.sresult.common.extensions.handle
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.base.*
import com.rasalexman.sresultpresentation.tools.DebouncedOnClickListener
import com.rasalexman.sresultpresentation.tools.ReflectionTextWatcher

fun View.setOnDebounceClickListener(debounceMillis: Long = 400L, listener: UnitHandler?) {
    setOnClickListener(object : DebouncedOnClickListener(debounceMillis) {
        override fun onDebouncedClick(v: View?) {
            listener?.invoke()
        }
    })
}

/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun Drawable.tint(@ColorInt color: Int) {
    val wrapDrawable = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTint(wrapDrawable, color)
    DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN)
}

var TextView.drawableStart: Drawable?
    get() = drawables[0]
    set(value) = setDrawables(value, drawableTop, drawableEnd, drawableBottom)

var TextView.drawableTop: Drawable?
    get() = drawables[1]
    set(value) = setDrawables(drawableStart, value, drawableEnd, drawableBottom)

var TextView.drawableEnd: Drawable?
    get() = drawables[2]
    set(value) = setDrawables(drawableStart, drawableTop, value, drawableBottom)

var TextView.drawableBottom: Drawable?
    get() = drawables[3]
    set(value) = setDrawables(drawableStart, drawableTop, drawableEnd, value)

private val TextView.drawables: Array<Drawable?>
    @SuppressLint("ObsoleteSdkInt")
    get() = if (Build.VERSION.SDK_INT >= 17) compoundDrawablesRelative else compoundDrawables

private fun TextView.setDrawables(start: Drawable?, top: Drawable?, end: Drawable?, bottom: Drawable?) {
    @SuppressLint("ObsoleteSdkInt")
    if (Build.VERSION.SDK_INT >= 17)
        setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
    else
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
}

fun View.toast(
    message: Any?,
    duration: Int = Toast.LENGTH_SHORT
) = context?.toast(message, duration)

fun Fragment.toast(
    message: Any?,
    duration: Int = Toast.LENGTH_SHORT
) = context?.toast(message, duration)

fun Fragment.alert(
    message: Any?,
    dialogTitle: Any? = null,
    okTitle: Any? = null,
    showCancel: Boolean = true,
    cancelTitle: Any? = null,
    cancelHandler: UnitHandler? = null,
    okHandler: UnitHandler? = null
) = context?.alert(message, dialogTitle, okTitle, showCancel, cancelTitle, cancelHandler, okHandler)

fun TextView.clear() {
    this.text = null
    this.setOnClickListener(null)
}

fun ImageView.clear() {
    this.setImageResource(0)
    this.setImageBitmap(null)
    this.setImageDrawable(null)
}

fun Button.clear(isClearText: Boolean = true) {
    if (isClearText) this.text = null
    this.setOnClickListener(null)
}

fun CheckBox.clear() {
    this.setOnCheckedChangeListener(null)
    this.setOnClickListener(null)
}

fun Toolbar.clear() {
    this.title = null
    this.subtitle = null
    this.setNavigationOnClickListener(null)
    this.setOnMenuItemClickListener(null)
}

fun AutoCompleteTextView.clear() {
    this.tag = null
    this.onFocusChangeListener = null
    this.onItemClickListener = null
    this.setOnEditorActionListener(null)
    this.setAdapter(null)
    ReflectionTextWatcher.removeAll(this)
}

fun RecyclerView.clear() {
    this.adapter = null
    this.tag = null
    this.layoutManager = null
    this.itemAnimator = null
    this.clearOnScrollListeners()
    this.clearOnChildAttachStateChangeListeners()
    val decorations = this.itemDecorationCount
    if(decorations > 0) {
        repeat(decorations) {
            this.removeItemDecorationAt(it)
        }
    }
}

fun ViewGroup.clear() {
    this.children.forEach {
        it.clearView()
    }
}

fun ViewPager.clear() {
    this.adapter = null
    this.tag = null
    this.clearOnPageChangeListeners()
}

fun ViewPager2.clear() {
    this.adapter = null
    this.tag = null

    val callbackKey = this.hashCode().toString()
    changeCallbackMap[callbackKey]?.let {
        it.onPageChangedCallback = null
        this.unregisterOnPageChangeCallback(it)
        changeCallbackMap.remove(callbackKey)
    }
}

fun TabLayout.clear() {
    this.clearOnTabSelectedListeners()
    this.removeAllTabs()
}

fun EditText.clear() {
    setOnClickListener(null)
    setOnEditorActionListener(null)
    onFocusChangeListener = null
    ReflectionTextWatcher.removeAll(this)
}

fun SeekBar.clear() {
    this.setOnSeekBarChangeListener(null)
}

fun LinearProgressIndicator.clear() {
    this.clearAnimation()
    this.isIndeterminate = false
}

fun Spinner.clear() {
    this.adapter = null
    this.onItemSelectedListener = null
    this.adapter = null
}

fun RatingBar.clear() {
    this.onRatingBarChangeListener = null
}

fun SearchView.clear() {
    setOnQueryTextListener(null)
    setOnCloseListener(null)
    setOnQueryTextFocusChangeListener(null)
    setOnSuggestionListener(null)
    setOnSearchClickListener(null)
}

fun View?.clearView() {
    when (this) {
        is ImageView -> this.clear()
        is Button -> this.clear()
        is RatingBar -> this.clear()
        is LinearProgressIndicator -> this.clear()
        is SearchView -> this.clear()
        is AutoCompleteTextView -> this.clear()
        is EditText -> this.clear()
        is TextView -> this.clear()
        is CheckBox -> this.clear()
        is Toolbar -> this.clear()
        is SeekBar -> this.clear()
        is Spinner -> this.clear()
        is TabLayout -> this.clear()
        is ViewPager -> this.clear()
        is ViewPager2 -> this.clear()
        is RecyclerView -> this.clear()
        is AdapterView<*> -> Unit
        is ViewGroup -> this.clear()
    }
}

fun View.hide(gone: Boolean = true) {
    visibility = if (gone) View.GONE else View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.setVisibility(isVisible: Boolean = true) {
    if(isVisible) this.show()
    else this.hide()
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun ISResultHandler.onBaseResultHandler(result: SResult<*>) {
    if (result.isHandled) return
    result.handle()

    when (result) {
        is SResult.Success -> (this as? ISuccessHandler)?.apply {
            hideLoading()
            showSuccess(result = result)
        }
        is SResult.Loading -> (this as? ILoadingHandler)?.showLoading()
        is SResult.Progress -> (this as? IProgressHandler)?.showProgress(result.progress, result.message)
        is SResult.Empty -> (this as? IEmptyHandler)?.apply {
            hideLoading()
            showEmptyLayout()
        }

        is SResult.Toast -> {
            (this as? IToastHandler)?.apply {
                hideLoading()
                showToast(result.message, result.interval)
            }
        }

        is SResult.AbstractFailure.Failure -> {
            loggE(exception = result.exception, message = result.message.toString())
            (this as? IFailureHandler)?.apply {
                hideLoading()
                if(result is SResult.AbstractFailure.Alert) {
                    showAlert(result)
                } else {
                    showFailure(result)
                }
            }
        }

        is SResult.NavigateResult.BaseNavigationResult -> {
            (this as? INavigateHandler)?.apply {
                hideLoading()

                when(result) {
                    is SResult.NavigateResult.NavigatePop -> navigatePop(
                        backArgs = result.args?.toBundle()
                    )
                    is SResult.NavigateResult.NavigatePopTo -> navigatePopTo(
                        navResId = result.navigateResourceId,
                        isInclusive = result.isInclusive,
                        backArgs = result.args?.toBundle()
                    )
                    is SResult.NavigateResult.NavigateBack -> onBackPressed()
                    is SResult.NavigateResult.NavigateNext -> onNextPressed()

                    else -> {
                        result.navDirection?.let { direction ->
                            navigateTo(direction)
                        }.or {
                            result.navigateResourceId?.let { navResId ->
                                navigateBy(navResId)
                            }
                        }
                    }
                }
            }
        }
        is SResult.AbstractFailure,
        is SResult.NothingResult -> (this as? ILoadingHandler)?.hideLoading()

        else -> Unit
    }
}

fun Toolbar.setNavigationIconColor(@ColorInt color: Int) = navigationIcon?.mutate()?.let {
    it.setTint(color)
    this.navigationIcon = it
}