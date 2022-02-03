package com.rasalexman.sresultexample.base

import androidx.lifecycle.*
import com.rasalexman.easyrecyclerbinding.ScrollPosition
import com.rasalexman.sresult.common.extensions.emptyResult
import com.rasalexman.sresult.common.extensions.getList
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.users.UserItem
import com.rasalexman.sresultpresentation.extensions.onEvent
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

abstract class BaseItemsViewModel : BaseViewModel() {

    protected val searchLD: MutableLiveData<String> by unsafeLazy {
        MutableLiveData("")
    }
    val scrollPosition: MutableLiveData<ScrollPosition> = MutableLiveData(ScrollPosition())
    override val eventLiveData: MutableLiveData<ISEvent> = MutableLiveData<ISEvent>(SEvent.Refresh)

    @OptIn(FlowPreview::class)
    private val searchQuery by unsafeLazy {
        searchLD.asFlow().debounce(200L).distinctUntilChanged()
    }

    override val resultLiveData: LiveData<ResultList<UserItem>> by unsafeLazy {
        onEvent<SEvent.Refresh, ResultList<UserItem>> {
            applyResultLiveData()
        }
    }

    open suspend fun LiveDataScope<ResultList<UserItem>>.applyResultLiveData() {
        processResultLiveData(searchQuery)?.let { liveDataResult ->
            emitSource(liveDataResult)
        } ?: processResultUseCase(searchQuery)?.let { useCaseResult ->
            emit(useCaseResult)
        } ?: processResultFlow(searchQuery)?.let { flowResult ->
            emitSource(flowResult.asLiveData())
        } ?: emit(emptyResult())
    }

   open val items: LiveData<List<UserItem>> by unsafeLazy {
        resultLiveData.asFlow().filter { it is SResult.Success }.asLiveData().distinctUntilChanged().map { result ->
            result.getList()
        }
    }

    protected open suspend fun LiveDataScope<ResultList<UserItem>>.processResultLiveData(searchFlow: Flow<String>): LiveData<ResultList<UserItem>>? = null
    protected open suspend fun LiveDataScope<ResultList<UserItem>>.processResultUseCase(searchFlow: Flow<String>): ResultList<UserItem>? = null
    protected open suspend fun LiveDataScope<ResultList<UserItem>>.processResultFlow(searchFlow: Flow<String>): FlowResultList<UserItem>? = null
}