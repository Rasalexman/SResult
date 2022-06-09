package com.rasalexman.sresultexample.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rasalexman.sresult.common.extensions.applyIfError
import com.rasalexman.sresult.common.extensions.applyIfSuccess
import com.rasalexman.sresult.common.extensions.applyIfType
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentCustomDialogBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingDialogFragment

class CustomDialogFragment : BaseBindingDialogFragment<FragmentCustomDialogBinding, CustomDialogViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_custom_dialog

    override val viewModel: CustomDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
            }
            setCancelable(false)
        }
    }

    override fun onResume() {
        super.onResume()
        requireDialog().setCancelable(false)
    }

    override fun onResultHandler(result: SResult<*>) {
        super.onResultHandler(result)
        result.applyIfType<SResult.NavigateResult.NavigateTo> {
            super.onBackPressed()
        }.applyIfSuccess {
            onBackPressed()
            postResult(COMPLETE_RESULT, it)
        }.applyIfError {
            onBackPressed()
            postResult(ERROR_RESULT, it.message)
        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack()
        return super.onBackPressed()
    }

    private fun postResult(key: String, data: Any? = null) {
        this.setFragmentResult(key, bundleOf(key to data))
    }

    override fun showNavigationError(e: Exception?, navResId: Int?) = Unit
    override fun showSuccess(result: SResult.Success<*>) = Unit

    companion object {
        const val COMPLETE_RESULT = "LoadingDialogFragment_complete"
        const val ERROR_RESULT = "LoadingDialogFragment_error"
    }

}