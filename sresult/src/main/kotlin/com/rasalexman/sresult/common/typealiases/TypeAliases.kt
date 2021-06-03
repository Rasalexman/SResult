package com.rasalexman.sresult.common.typealiases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.data.dto.ISResult
import com.rasalexman.sresult.data.dto.SResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

typealias UnitHandler = () -> Unit
typealias InHandler<T> = (T) -> Unit
typealias DoubleInHandler<T, P> = (T, P) -> Unit
typealias OutHandler<T> = () -> T
typealias InSameOutHandler<T> = (T) -> T
typealias InOutHandler<T, R> = (T) -> R

typealias SUnitHandler = suspend () -> Unit
typealias SInHandler<T> = suspend (T) -> Unit
typealias SParentInHandler<P, T> = suspend P.(T) -> Unit
typealias SOutHandler<T> = suspend () -> T
typealias SInSameOutHandler<T> = suspend (T) -> T
typealias SInOutHandler<T, R> = suspend (T) -> R
typealias SDoubleInOutHandler<T, R, P> = suspend (T, R) -> P
typealias SParentInOutHandler<P, T, R> = suspend P.(T) -> R

typealias FlowResult<T> = Flow<SResult<T>>
typealias FlowList<T> = Flow<List<T>>
typealias FlowResultList<T> = Flow<SResult<List<T>>>
typealias SharedFlowList<T> = SharedFlow<List<T>>
typealias SharedFlowResultList<T> = SharedFlow<SResult<List<T>>>
typealias MutableSharedFlowList<T> = MutableSharedFlow<List<T>>
typealias MutableStateFlowList<T> = MutableStateFlow<List<T>>
typealias MutableStateFlowResultList<T> = MutableStateFlow<SResult<List<T>>>

typealias AnyResult = ISResult<Any>
typealias AnyResultLiveData = LiveData<AnyResult>
typealias AnyResultMutableLiveData = MutableLiveData<AnyResult>
typealias ResultLiveData<T> = LiveData<ISResult<T>>
typealias ResultListLiveData<T> = LiveData<ISResult<List<T>>>
typealias ResultMutableLiveData<T> = MutableLiveData<ISResult<T>>

typealias ResultList<T> = ISResult<List<T>>
typealias ResultMutableList<T> = ISResult<MutableList<T>>
typealias ResultInHandler<T> = (ISResult<T>) -> Unit
typealias ResultInOutHandler<T, R> = (ISResult<T>) -> ISResult<R>

typealias ResultCloseDialog = SResult.NavigateResult.CloseBottomSheet
