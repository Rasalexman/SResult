package com.rasalexman.sresultexample.users

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.debounce
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

class UsersViewModel : BaseItemsViewModel() {

    private val searchLD: MutableLiveData<String> by unsafeLazy {
        MutableLiveData("")
    }

    @FlowPreview
    override val items: LiveData<List<UserItem>> by unsafeLazy {
        super.items.switchMap { items ->
            asyncLiveData {
                searchLD.asFlow().debounce(200L).distinctUntilChanged().collect { query ->
                    val result = if(query.isNotEmpty() && items.isNotEmpty()) {
                        items.filter { it.fullName.contains(query , true) }
                    } else {
                        items
                    }
                    emit(result)
                }
            }
        }
    }

    fun onItemClicked(item: UserItem) {
        navigationLiveData.value = MainFragmentDirections.showProfileFragment(itemId = item.id, userItem = item).toNavigateResult()
    }

    fun onSearch(it: String) {
        searchLD.value = it
    }

    fun cancelSearch() {

    }
}