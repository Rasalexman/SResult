package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface ISResultHandler {
    fun onResultHandler(result: SResult<*>)
}