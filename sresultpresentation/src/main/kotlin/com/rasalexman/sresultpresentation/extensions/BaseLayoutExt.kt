package com.rasalexman.sresultpresentation.extensions

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.rasalexman.sresultpresentation.databinding.BaseBindingLayout
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import com.rasalexman.sresultpresentation.viewModels.CustomViewModelLazy

inline fun<reified F : BaseFragment<*>, reified VM : BaseViewModel> BaseBindingLayout<out ViewDataBinding, VM>.viewModels(
    noinline fragmentProducer: () -> F = { this.findFragment() },
    noinline storeProducer: (() -> ViewModelStore)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    return CustomViewModelLazy(VM::class, fragmentProducer, storeProducer, factoryProducer)
}
