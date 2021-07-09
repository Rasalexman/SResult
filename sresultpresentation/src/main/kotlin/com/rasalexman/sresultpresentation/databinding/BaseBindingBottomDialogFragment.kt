package com.rasalexman.sresultpresentation.databinding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rasalexman.easyrecyclerbinding.createBindingWithViewModel
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.dialogs.BaseDialogFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlin.properties.Delegates

abstract class BaseBindingBottomDialogFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseDialogFragment<VM>(),
    IBaseBindingFragment<B, VM> {

    private var _currentBinding: B? = null
    override val binding: B
        get() = _currentBinding ?: throw NullPointerException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewModel?.let { vm ->
            createBindingWithViewModel<B, VM>(
                layoutId,
                vm,
                BR.vm,
                container,
                false
            ).also {
                _currentBinding = it
                initBinding(it)
            }.root
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun initBinding(binding: B) = Unit

    override fun onDestroyView() {
        _currentBinding?.unbind()
        _currentBinding = null
        super.onDestroyView()
    }
}