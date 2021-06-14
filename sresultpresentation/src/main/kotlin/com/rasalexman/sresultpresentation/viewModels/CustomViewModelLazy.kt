package com.rasalexman.sresultpresentation.viewModels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

class CustomViewModelLazy<VM : BaseViewModel>(
    private val viewModelClass: KClass<VM>,
    private val fragmentProducer: () -> Fragment
) : Lazy<VM> {

    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val parentFragment: Fragment = fragmentProducer()
                val factory = parentFragment.defaultViewModelProviderFactory
                ViewModelProvider(parentFragment, factory).get(viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}