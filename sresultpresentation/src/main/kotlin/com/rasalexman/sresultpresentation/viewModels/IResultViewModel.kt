package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.sresult.data.dto.ISEvent

interface IResultViewModel {
    fun processViewEvent(viewEvent: ISEvent)
    fun processEventAsync(viewEvent: ISEvent)
}