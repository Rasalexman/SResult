package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface ISResultHandler : IBaseHandler {
    fun onResultHandler(result: SResult<*>)
}