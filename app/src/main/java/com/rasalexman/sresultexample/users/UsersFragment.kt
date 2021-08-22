package com.rasalexman.sresultexample.users

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.rasalexman.easyrecyclerbinding.DiffCallback
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentUsersBinding
import com.rasalexman.sresultexample.databinding.ItemUserBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.sresultpresentation.extensions.color
import com.rasalexman.sresultpresentation.extensions.refresh

class UsersFragment : BaseBindingFragment<FragmentUsersBinding, UsersViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_users

    override val needBackButton: Boolean
        get() = true

    override val toolbarMenuId: Int
        get() = R.menu.menu_refresh

    override val toolbarNavigationIconColor: Int
        get() = color(R.color.black)

    override val toolbarView: Toolbar
        get() = binding.toolbarLayout

    override val loadingViewLayout: View
        get() = binding.loadingLayout

    override val viewModel: UsersViewModel by viewModels()

    override fun initBinding(binding: FragmentUsersBinding) {
        super.initBinding(binding)

        binding.rvConfig = createRecyclerConfig<UserItem, ItemUserBinding> {
            itemId = BR.item
            layoutId = R.layout.item_user
            onItemClick = { item, _ ->
                viewModel.onItemClicked(item)
            }

            diffUtilCallback = object : DiffCallback<UserItem>() {
                override fun areItemsTheSame(
                    oldItem: UserItem,
                    newItem: UserItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        }
        setupSearchView(binding.searchView)
    }

    private fun setupSearchView(searchView: SearchView) {

        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateSearchRequest(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateSearchRequest(newText)
                return true
            }
        })

        searchView.setOnCloseListener {
            onCloseClickHandler()
            return@setOnCloseListener true
        }
    }

    private fun onCloseClickHandler() {
        viewModel.cancelSearch()
        updateSearchViewText()
    }

    private fun updateSearchRequest(query: String?) {
        query?.let {
            viewModel.onSearch(it)
        }
    }

    private fun updateSearchViewText(query: String = "") {
        currentBinding?.searchView?.setQuery(query, false)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionRefresh) {
            refresh()
        }
        return super.onMenuItemClick(item)
    }

    override fun onDestroyView() {
        binding.searchView.apply {
            setOnCloseListener(null)
            setOnQueryTextListener(null)
        }
        super.onDestroyView()
    }
}