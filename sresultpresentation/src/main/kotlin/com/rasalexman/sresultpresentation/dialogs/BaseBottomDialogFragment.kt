package com.rasalexman.sresultpresentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

abstract class BaseBottomDialogFragment<VM : IBaseViewModel> : BaseDialogFragment<VM>() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }
}