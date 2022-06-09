package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult

interface IEventableViewModel {
    fun processEvent(viewEvent: com.rasalexman.sresult.data.dto.ISEvent)
    fun processEventAsync(viewEvent: com.rasalexman.sresult.data.dto.ISEvent)

    fun handleErrorState(errorResult: com.rasalexman.sresult.data.dto.SResult.AbstractFailure)
}