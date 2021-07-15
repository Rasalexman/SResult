package com.rasalexman.sresultpresentation.databinding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rasalexman.sresultpresentation.dialogs.BaseDialogFragment
import com.rasalexman.sresultpresentation.extensions.setupBinding
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

abstract class BaseBindingBottomDialogFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseDialogFragment<VM>(),
    IBaseBindingFragment<B, VM> {

    override var currentBinding: B? = null
    override val binding: B
        get() = currentBinding ?: throw NullPointerException("Binding is not initialized")

    protected val bottomBehavior: BottomSheetBehavior<*>
        get() = (dialog as BottomSheetDialog).behavior

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setupBindingView(inflater, container)
    }

    override fun setupBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        return setupBinding<B, VM>(inflater, container)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun initBinding(binding: B) = Unit

    override fun onDestroyView() {
        currentBinding?.unbind()
        currentBinding = null
        super.onDestroyView()
    }
}