package com.rasalexman.sresultexample.users

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel

class UsersViewModel : BaseItemsViewModel() {

    private val searchVM: MutableLiveData<String> = MutableLiveData()

    fun onItemClicked(item: UserItem) {
        navigationLiveData.value = MainFragmentDirections.showProfileFragment(itemId = item.id, userItem = item).toNavigateResult()
    }

    fun onSearch(it: String) {
        searchVM.postValue(it)
    }

    fun cancelSearch() {

    }
}