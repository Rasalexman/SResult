package com.rasalexman.sresultpresentation.databinding

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel

abstract class BaseBindingBottomDialogFragment<B : ViewDataBinding, VM : BaseContextViewModel> :
    BaseBindingDialogFragment<B, VM>() {

    protected val bottomBehavior: BottomSheetBehavior<*>
        get() = (dialog as BottomSheetDialog).behavior

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }
}