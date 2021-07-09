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
import kotlin.properties.Delegates

abstract class BaseBindingFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseFragment<VM>(),
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

    /** starts in onCreateView */
    override fun initBinding(binding: B) = Unit

    override fun onDestroyView() {
        _currentBinding?.unbind()
        _currentBinding = null
        super.onDestroyView()
    }
}