package com.rasalexman.sresultpresentation.extensions

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.typealiases.UnitHandler
import com.rasalexman.sresultpresentation.R


private var alertDialog: Dialog? = null

fun Context.toast(message: Any?, duration: Int = Toast.LENGTH_SHORT) {
    when (message) {
        is String -> {
            if (message.isNotEmpty())
                Toast.makeText(this, message, duration).show()
        }
        is Int -> {
            if (message > 0) Toast.makeText(this, message, duration).show()
        }
        else -> Unit
    }
}

fun Context?.closeAlert(dismiss: Boolean = true) {
    if (dismiss) alertDialog?.dismiss()
    alertDialog = null
}

fun Context.alert(
    message: Any?,
    dialogTitle: Any? = null,
    okTitle: Any? = null,
    showCancel: Boolean = true,
    cancelTitle: Any? = null,
    cancelHandler: UnitHandler? = null,
    okHandler: UnitHandler? = null
) {
    closeAlert()

    val okTitleString = when (okTitle) {
        is String -> okTitle
        is Int -> string(okTitle)
        else -> string(R.string.title_ok)
    }

    val cancelTitleString = when (cancelTitle) {
        is String -> cancelTitle
        is Int -> string(cancelTitle)
        else -> string(R.string.title_cancel)
    }

    alertDialog = AlertDialog
        .Builder(this)
        .setCancelable(false)
        .apply {

        }
        .setPositiveButton(okTitleString) { dialogInterface, _ ->
            dialogInterface.dismiss()
            closeAlert(dismiss = false)
            okHandler?.invoke()
        }.applyIf(showCancel) {
            it.setNegativeButton(cancelTitleString) { dialogInterface, _ ->
                dialogInterface.dismiss()
                closeAlert(dismiss = false)
                cancelHandler?.invoke()
            }
        }.run {
            when (dialogTitle) {
                is String -> setTitle(dialogTitle)
                is Int -> setTitle(dialogTitle)
                else -> setTitle(R.string.title_warning)
            }

            when (message) {
                is String -> setMessage(message)
                is Int -> setMessage(message)
                else -> setMessage(message.takeIf { it != null }?.toString() ?: "")
            }
            show()
        }
}

///----- DIMENSIONS
//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

fun Context.color(@ColorRes resource: Int): Int = ContextCompat.getColor(this, resource)
fun Context.string(@StringRes stringRes: Int): String = this.getString(stringRes)
fun Context.drawable(@DrawableRes drawableResId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableResId)

fun Context.drawableWithParams(
    @DrawableRes drawableResId: Int,
    paramsInitializer: (Drawable?) -> Unit
): Drawable? {
    val itemDrawable: Drawable? = ContextCompat.getDrawable(this, drawableResId)
    paramsInitializer(itemDrawable)
    return itemDrawable
}