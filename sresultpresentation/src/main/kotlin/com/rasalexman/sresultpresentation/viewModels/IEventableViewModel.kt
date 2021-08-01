package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult

interface IEventableViewModel {
    fun processEvent(viewEvent: ISEvent)
    fun processEventAsync(viewEvent: ISEvent)

    fun handleErrorState(errorResult: SResult.AbstractFailure)
}