package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SResult

interface IResultViewModel {
    fun processViewEvent(viewEvent: ISEvent)
    fun processEventAsync(viewEvent: ISEvent)

    fun handleErrorState(errorResult: SResult.AbstractFailure)
}