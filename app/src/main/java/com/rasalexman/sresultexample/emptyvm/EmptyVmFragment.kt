package com.rasalexman.sresultexample.emptyvm

import android.os.Bundle
import com.rasalexman.sresult.common.extensions.navigatePopTo
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultexample.NavigationMainDirections
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.bottomrecycler.BottomRecyclerFragment
import com.rasalexman.sresultexample.databinding.FragmentEmptyVmBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.sresultpresentation.extensions.string
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class EmptyVmFragment : BaseBindingFragment<FragmentEmptyVmBinding, BaseViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_empty_vm

    override val needBackButton: Boolean
        get() = true

    override val toolbarTitle: String
        get() = string(R.string.title_empty_vm)

    override fun initBinding(binding: FragmentEmptyVmBinding) {
        binding.recyclerBottomButton.setOnClickListener {
            onResultHandler(NavigationMainDirections.showBottomRecyclerFragment().toNavigateResult())
        }
        binding.customDialogButton.setOnClickListener {
            onResultHandler(NavigationMainDirections.showCustomDialogFragment().toNavigateResult())
        }
        binding.backArgsButton.setOnClickListener {
            onResultHandler(navigatePopTo(R.id.mainFragment, backArgs = mapOf("custom_args" to "it_works")))
        }
    }

    override fun onBackArgumentsHandler(backArgs: Bundle) {
        backArgs.getString(BottomRecyclerFragment.KEY_SELECTED)?.let {
            binding.selectedTV.text = it
        }
    }

}