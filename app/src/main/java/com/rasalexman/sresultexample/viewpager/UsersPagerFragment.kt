package com.rasalexman.sresultexample.viewpager

import androidx.fragment.app.viewModels
import com.rasalexman.easyrecyclerbinding.createRecyclerMultiConfig
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentVpUsersBinding
import com.rasalexman.sresultexample.viewpager.pages.FirstPageViewModel
import com.rasalexman.sresultexample.viewpager.pages.SecondPageViewModel
import com.rasalexman.sresultpresentation.databinding.BasePagerBindingFragment
import com.rasalexman.sresultpresentation.viewModels.IEventableViewModel

class UsersPagerFragment : BasePagerBindingFragment<FragmentVpUsersBinding, UserPagerViewModel>() {
    override val layoutId: Int get() = R.layout.fragment_vp_users
    override val viewModel: UserPagerViewModel by viewModels()

    override val needBackButton: Boolean
        get() = true

    override val pageTitles: Array<String> = arrayOf("Login", "With Recycler")
    override val pagesVMList: List<IEventableViewModel> by lazy {
        listOf(firstPageViewModel, secondPageViewModel)
    }

    private val firstPageViewModel: FirstPageViewModel by viewModels()
    private val secondPageViewModel: SecondPageViewModel by viewModels()

    override fun initBinding(binding: FragmentVpUsersBinding) {
        super.initBinding(binding)
        setupTabMediator(binding.tabLayout, binding.viewpager2)
    }

    override fun setupViewPagerConfig(binding: FragmentVpUsersBinding) {
        binding.vpConfig = createRecyclerMultiConfig {
            itemId = BR.vm
        }
    }
}