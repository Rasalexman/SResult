package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.rasalexman.sresultpresentation.fragments.BaseFragment
import kotlin.reflect.KClass

class CustomViewModelLazy<F : BaseFragment<*>, VM : BaseViewModel>(
    private val viewModelClass: KClass<VM>,
    private val fragmentProducer: () -> F,
    private val storeProducer: (() -> ViewModelStore)? = null,
    private val factoryProducer: (() -> ViewModelProvider.Factory)? = null
) : Lazy<VM> {

    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val parentFragment: F = fragmentProducer()
                val factory = factoryProducer?.invoke() ?: parentFragment.defaultViewModelProviderFactory
                val store = storeProducer?.invoke() ?: parentFragment.viewModelStore
                ViewModelProvider(store, factory).get(viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}