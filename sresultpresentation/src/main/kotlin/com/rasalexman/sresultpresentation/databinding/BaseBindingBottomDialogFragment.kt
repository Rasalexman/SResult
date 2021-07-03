package com.rasalexman.sresultpresentation.databinding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.ViewDataBinding
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rasalexman.easyrecyclerbinding.createBindingWithViewModel
import com.rasalexman.sresult.common.extensions.applyIfType
import com.rasalexman.sresult.common.typealiases.ResultCloseDialog
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.dialogs.BaseDialogFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlin.properties.Delegates

abstract class BaseBindingBottomDialogFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseDialogFragment<VM>(),
    IBaseBindingFragment<B, VM> {

    override var binding: B by Delegates.notNull<B>()

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
                binding = it
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

    override fun onResultHandler(result: SResult<*>) {
        super.onResultHandler(result)
        result.applyIfType<ResultCloseDialog> { closeDialog() }
    }

    private fun closeDialog() {
        Navigation.findNavController(
            requireActivity(),
            R.id.mainHostFragment
        ).popBackStack()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}