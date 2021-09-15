package com.rasalexman.sresultpresentation.databinding

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultpresentation.extensions.addOnCreateViewModelObservers
import com.rasalexman.sresultpresentation.extensions.clearViewModel
import com.rasalexman.sresultpresentation.extensions.stringArr
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.IBasePagerViewModel
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel

abstract class BasePagerBindingFragment<B : ViewDataBinding, VM : BaseContextViewModel> :
    BaseBindingFragment<B, VM>() {

    /**
     * Pages titles string array resource id
     */
    protected open val pagesTitleArrayResId: Int = -1

    protected open val pageTitles: Array<String> by unsafeLazy {
        if (pagesTitleArrayResId > 0) stringArr(pagesTitleArrayResId)
        else emptyArray()
    }

    protected open var tabLayoutMediator: TabLayoutMediator? = null
    abstract val pagesVMList: List<IEventableViewModel>

    override fun initBinding(binding: B) {
        super.initBinding(binding)
        setupPagesItems()
        setupViewPagerConfig(binding)
    }

    abstract fun setupViewPagerConfig(binding: B)

    /**
     * Setup pager view models
     */
    protected open fun addPagesViewModelsToObserve(vmList: List<IEventableViewModel>) {
        vmList.forEach {
            addOnCreateViewModelObservers(it)
        }
    }

    /**
     *
     */
    protected open fun setupTabMediator(tabLayout: TabLayout, viewPager: ViewPager2) {
        tabLayoutMediator =
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                setupTab(tab, position)
            }
    }

    /**
     *
     */
    protected open fun setupTab(tab: TabLayout.Tab, position: Int) {
        tab.text = pageTitles.getOrNull(position).orEmpty()
    }

    /**
     * This function add to observe inner viewPager pages viewModels
     */
    protected open fun setupPagesItems() {
        addPagesViewModelsToObserve(pagesVMList)
        (viewModel as? IBasePagerViewModel)?.items = pagesVMList
    }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        attachViewPagerMediator()
    }

    protected open fun attachViewPagerMediator() {
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
            pageVM.clearViewModel(this.viewLifecycleOwner)
        }
        super.onDestroyView()
    }
}