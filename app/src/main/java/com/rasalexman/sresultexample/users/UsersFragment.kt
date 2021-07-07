package com.rasalexman.sresultexample.users

import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.rasalexman.easyrecyclerbinding.DiffCallback
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentUsersBinding
import com.rasalexman.sresultexample.databinding.ItemUserBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.sresultpresentation.extensions.fetch
import com.rasalexman.sresultpresentation.extensions.refresh
import com.rasalexman.sresultpresentation.extensions.string

class UsersFragment : BaseBindingFragment<FragmentUsersBinding, UsersViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_users

    override val needBackButton: Boolean
        get() = true

    override val toolbarTitle: String
        get() = string(R.string.title_users)

    override val toolbarMenuId: Int
        get() = R.menu.menu_refresh

    override val viewModel: UsersViewModel by viewModels()

    override fun initBinding(binding: FragmentUsersBinding) {
        super.initBinding(binding)

        binding.rvConfig = createRecyclerConfig<UserItem, ItemUserBinding> {
            itemId = BR.item
            layoutId = R.layout.item_user
            onItemClick = { item, pos ->
                viewModel.onItemClicked(item)
            }

            /*diffUtilCallback = object : DiffCallback<UserItem>() {
                override fun areItemsTheSame(
                    oldItem: UserItem?,
                    newItem: UserItem?
                ): Boolean {
                    return if (oldItem != null && newItem != null) {
                        oldItem.id == newItem.id
                    } else {
                        oldItem == null && newItem == null
                    }
                }
            }*/
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if(item.itemId == R.id.actionRefresh) {
            refresh()
        }
        return super.onMenuItemClick(item)
    }
}