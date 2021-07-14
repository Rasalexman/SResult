package com.rasalexman.sresultpresentation.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.rasalexman.easyrecyclerbinding.createBindingWithViewModel
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

abstract class BaseBindingFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseFragment<VM>(),
    IBaseBindingFragment<B, VM> {

    protected var currentBinding: B? = null
    override val binding: B
            get() = currentBinding ?: throw NullPointerException("Binding is not initialized")
    
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
                currentBinding = it
                initBinding(it)
            }.root
        }
    }

    /** starts in onCreateView */
    override fun initBinding(binding: B) = Unit

    override fun onDestroyView() {
        currentBinding?.unbind()
        currentBinding = null
        super.onDestroyView()
    }
}