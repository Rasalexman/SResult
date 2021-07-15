package com.rasalexman.sresultexample.users

import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel

class UsersViewModel : BaseItemsViewModel() {

    fun onItemClicked(item: UserItem) {
        navigationLiveData.value = MainFragmentDirections.showProfileFragment(itemId = item.id, userItem = item).toNavigateResult()
    }
}