package com.rasalexman.sresultpresentation.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel

interface IBaseBindingFragment<B : ViewDataBinding, VM : IEventableViewModel> : IBaseFragment<VM> {
    var currentBinding: B?
    val binding: B
    val needCallPending: Boolean

    fun setupBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View

    fun initBinding(binding: B)
}