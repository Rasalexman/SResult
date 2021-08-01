package com.rasalexman.sresultpresentation.fragments

import com.rasalexman.sresultpresentation.extensions.observeBaseViewModel
import com.rasalexman.sresultpresentation.extensions.observeStateViewModel
import com.rasalexman.sresultpresentation.extensions.setupToolbarSubtitle
import com.rasalexman.sresultpresentation.extensions.setupToolbarTitle
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel
import com.rasalexman.sresultpresentation.viewModels.IResultViewModel
import com.rasalexman.sresultpresentation.viewModels.IStateViewModel

abstract class BaseFragment<VM : IResultViewModel> : BaseResultFragment<VM>() {

    override fun addViewModelObservers(vm: IResultViewModel?) {
        when(vm) {
            is IBaseViewModel -> observeBaseViewModel(vm)
            is IStateViewModel -> observeStateViewModel(vm)
        }
    }

    /**
     * Any data types handler for [IBaseViewModel.anyLiveData]
     */
    override fun onAnyDataHandler(data: Any?) = Unit

    override fun toolbarTitleHandler(title: String) {
        toolbarView?.setupToolbarTitle(title, toolbarTitleResId, centerToolbarTitle)
    }
    override fun toolbarSubTitleHandler(subtitle: String) {
        toolbarView?.setupToolbarSubtitle(subtitle, centerToolbarTitle)
    }
}
