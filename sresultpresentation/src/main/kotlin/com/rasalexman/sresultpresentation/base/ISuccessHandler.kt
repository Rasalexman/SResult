package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface ISuccessHandler : ILoadingHandler {
    fun showSuccess(result: com.rasalexman.sresult.data.dto.SResult.Success<*>)
}