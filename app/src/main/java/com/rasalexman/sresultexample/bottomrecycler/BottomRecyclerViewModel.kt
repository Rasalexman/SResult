package com.rasalexman.sresultexample.bottomrecycler

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.loadingResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.base.BaseItemsViewModel
import com.rasalexman.sresultexample.users.ISearchUserItemsUseCase
import com.rasalexman.sresultexample.users.SearchUserItemsUseCase
import com.rasalexman.sresultexample.users.UserItem
import com.rasalexman.sresultpresentation.extensions.mutableMap
import kotlinx.coroutines.flow.Flow

class BottomRecyclerViewModel : BaseItemsViewModel() {

    private val getUsersListUseCase: ISearchUserItemsUseCase = SearchUserItemsUseCase()

    override val toolbarTitle: MutableLiveData<String> by unsafeLazy {
        items.mutableMap {
            string(R.string.title_bottom_recycler) + " ${it.size}"
        }
    }

    override suspend fun LiveDataScope<ResultList<UserItem>>.processResultFlow(
        searchFlow: Flow<String>
    ): FlowResultList<UserItem> {
        emit(loadingResult())
        return getUsersListUseCase(searchFlow)
    }
}