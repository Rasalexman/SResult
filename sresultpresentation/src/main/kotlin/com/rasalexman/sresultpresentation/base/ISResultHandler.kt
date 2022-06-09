package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface ISResultHandler {
    fun onResultHandler(result: com.rasalexman.sresult.data.dto.SResult<*>)
}