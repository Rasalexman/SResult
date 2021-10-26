package com.rasalexman.sresultexample.users

import androidx.lifecycle.*
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel
import com.rasalexman.sresultpresentation.extensions.AnyResultMutableLiveData
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.onEventMutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

class UsersViewModel : BaseItemsViewModel() {

    private val getUsersListUseCase: ISearchUserItemsUseCase = SearchUserItemsUseCase()

   /* override val items: LiveData<List<UserItem>> by unsafeLazy {
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
    }*/

    override val supportLiveData: AnyResultMutableLiveData by unsafeLazy {
        onEventMutable<UserClickEvent, AnyResult> {
            val navResult = MainFragmentDirections.showProfileFragment(itemId = it.id, userItem = it.item).toNavigateResult()
            emit(navResult)
        }
    }

    override suspend fun LiveDataScope<ResultList<UserItem>>.processResultFlow(searchFlow: Flow<String>): FlowResultList<UserItem>? {
        return getUsersListUseCase(searchFlow)
    }

    fun onItemClicked(item: UserItem) {
        processEvent(UserClickEvent(item.id, item))
    }

    fun onSearch(it: String) {
        searchLD.value = it
    }

    fun cancelSearch() {

    }

    class UserClickEvent(val id: String, val item: UserItem) : ISEvent
}