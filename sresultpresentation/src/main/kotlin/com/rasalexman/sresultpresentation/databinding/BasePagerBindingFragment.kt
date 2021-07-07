package com.rasalexman.sresultpresentation.databinding

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResultLiveData
import com.rasalexman.sresultpresentation.extensions.onAnyChange
import com.rasalexman.sresultpresentation.extensions.stringArr
import com.rasalexman.sresultpresentation.viewModels.BasePageViewModel
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import com.rasalexman.sresultpresentation.viewModels.BaseViewPagerViewModel

abstract class BasePagerBindingFragment<B : ViewDataBinding, VM : BaseViewPagerViewModel> :
    BaseBindingFragment<B, VM>() {

    open val pagesTitleArrayResId: Int = -1

    open val pageTitles: Array<String> by unsafeLazy {
        if(pagesTitleArrayResId > 0) stringArr(pagesTitleArrayResId)
        else emptyArray()
    }

    private var tabLayoutMediator: TabLayoutMediator? = null
    abstract val pagesVMList: List<BaseViewModel>

    override fun initBinding(binding: B) {
        super.initBinding(binding)
        setupPagesItems()
        setupViewPagerConfig(binding)
    }

    abstract fun setupViewPagerConfig(binding: B)

    @Suppress("UNCHECKED_CAST")
    protected fun addViewModelsToObserve(vmList: List<BaseViewModel>) {
        vmList.forEach { pageVM ->
            (pageVM.resultLiveData as? AnyResultLiveData)?.apply(::observeResultLiveData)
            (pageVM.supportLiveData as? AnyResultLiveData)?.apply(::observeResultLiveData)
            pageVM.navigationLiveData.apply(::observeNavigationLiveData)
            pageVM.anyLiveData?.apply(::observeAnyLiveData)

            pageVM.liveDataToObserve.forEach {
                onAnyChange(it)
            }
        }
    }

    protected open fun setupTabMediator(tabLayout: TabLayout, viewPager: ViewPager2) {
        tabLayoutMediator =
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setText(pageTitles.getOrNull(position).orEmpty())
            }
    }

    /**
     * This function add to observe inner viewPager pages viewModels
     */
    private fun setupPagesItems() {
        addViewModelsToObserve(pagesVMList)
        viewModel?.items?.value = pagesVMList
    }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        attachViewPagerMediator()
    }

    private fun attachViewPagerMediator() {
        tabLayoutMediator?.let {
            if (!it.isAttached) {
                it.attach()
            }
        }
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        pagesVMList.forEach { pageVM ->
            pageVM.resultLiveData?.removeObservers(this.viewLifecycleOwner)
            pageVM.supportLiveData.removeObservers(this.viewLifecycleOwner)
            pageVM.navigationLiveData.removeObservers(this.viewLifecycleOwner)
            pageVM.anyLiveData?.removeObservers(this.viewLifecycleOwner)
            pageVM.liveDataToObserve.forEach {
                it.removeObservers(this.viewLifecycleOwner)
            }
        }
        super.onDestroyView()
    }
}