package com.rasalexman.sresultexample.users

import androidx.lifecycle.LiveDataScope
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.NavigationMainDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel
import com.rasalexman.sresultpresentation.extensions.AnyResultMutableLiveData
import com.rasalexman.sresultpresentation.extensions.onEventMutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class UsersViewModel : BaseItemsViewModel() {

    private val getUsersListUseCase: ISearchUserItemsUseCase = SearchUserItemsUseCase()

    override val supportLiveData: AnyResultMutableLiveData by unsafeLazy {
        onEventMutable<UserClickEvent, AnyResult> {
            val sresult = anySuccess()
            val flattedResult = sresult.flatMapIfNotType<SResult.AbstractResult<Any>> {
                toastResult("Hello Im not this type")
            }
            supportLiveData.postValue(flattedResult)

            val navResult = NavigationMainDirections.showProfileFragment(itemId = it.id, userItem = it.item).toNavigateResult()
            emit(navResult)
        }
    }

    override suspend fun LiveDataScope<ResultList<UserItem>>.processResultFlow(searchFlow: Flow<String>): FlowResultList<UserItem> {
        return getUsersListUseCase(searchFlow).flowOn(Dispatchers.IO)
    }

    fun onItemClicked(item: UserItem) {
        processEvent(UserClickEvent(item.id, item))
    }

    fun onSearch(query: String) {
        searchLD.value = query
    }

    fun cancelSearch() {

    }

    data class UserClickEvent(val id: String, val item: UserItem) : ISEvent
}