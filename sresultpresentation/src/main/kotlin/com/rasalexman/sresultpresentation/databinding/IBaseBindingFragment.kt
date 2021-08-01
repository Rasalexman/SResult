package com.rasalexman.sresultpresentation.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IResultViewModel

interface IBaseBindingFragment<B : ViewDataBinding, VM : IResultViewModel> : IBaseFragment<VM> {
    var currentBinding: B?
    val binding: B

    fun setupBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View

    fun initBinding(binding: B)
}