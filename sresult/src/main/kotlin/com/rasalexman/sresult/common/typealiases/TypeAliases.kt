package com.rasalexman.sresult.common.typealiases

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
typealias FlowAnyResult = Flow<AnyResult>
typealias FlowList<T> = Flow<List<T>>
typealias FlowResultList<T> = Flow<SResult<List<T>>>
typealias SharedFlowList<T> = SharedFlow<List<T>>
typealias SharedFlowResultList<T> = SharedFlow<SResult<List<T>>>
typealias MutableSharedFlowList<T> = MutableSharedFlow<List<T>>
typealias MutableStateFlowList<T> = MutableStateFlow<List<T>>
typealias MutableStateFlowResultList<T> = MutableStateFlow<SResult<List<T>>>

typealias AnyResult = SResult<Any>

typealias ResultList<T> = SResult<List<T>>
typealias ResultMutableList<T> = SResult<MutableList<T>>
typealias ResultInHandler<T> = (SResult<T>) -> Unit
typealias ResultInOutHandler<T, R> = (SResult<T>) -> SResult<R>
