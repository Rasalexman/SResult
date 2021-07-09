package com.rasalexman.sresultpresentation.databinding

import androidx.databinding.ViewDataBinding
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

interface IBaseBindingFragment<B : ViewDataBinding, VM : IBaseViewModel> : IBaseFragment<VM> {
    val binding: B
    fun initBinding(binding: B)
}