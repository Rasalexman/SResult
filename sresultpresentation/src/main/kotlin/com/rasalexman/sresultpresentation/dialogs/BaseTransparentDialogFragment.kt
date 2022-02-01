package com.rasalexman.sresultpresentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.databinding.BaseBindingDialogFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

abstract class BaseTransparentDialogFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseBindingDialogFragment<B, VM>() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply { setBackgroundDrawableResource(android.R.color.transparent) }
            setCancelable(false)
        }
    }

    override fun onResume() {
        super.onResume()
        requireDialog().setCancelable(false)
        requireDialog().window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun showNavigationError(e: Exception?, navResId: Int?) = Unit
    override fun showSuccess(result: SResult.Success<*>) = Unit
    override fun closeContextAlert() = Unit
}