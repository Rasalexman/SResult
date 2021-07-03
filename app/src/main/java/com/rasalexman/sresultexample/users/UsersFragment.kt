package com.rasalexman.sresultexample.users

import androidx.fragment.app.viewModels
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentUsersBinding
import com.rasalexman.sresultexample.databinding.ItemUserBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.sresultpresentation.extensions.string

class UsersFragment : BaseBindingFragment<FragmentUsersBinding, UsersViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_users

    override val needBackButton: Boolean
        get() = true

    override val toolbarTitle: String
        get() = string(R.string.title_users)

    override val viewModel: UsersViewModel by viewModels()

    override fun initBinding(binding: FragmentUsersBinding) {
        super.initBinding(binding)

        binding.rvConfig = createRecyclerConfig<UserItem, ItemUserBinding> {
            itemId = BR.item
            layoutId = R.layout.item_user
            onItemClick = { item, pos ->
                viewModel.onItemClicked(item)
            }
        }
    }
}