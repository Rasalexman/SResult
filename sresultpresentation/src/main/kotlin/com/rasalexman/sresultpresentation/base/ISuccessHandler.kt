package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface ISuccessHandler : IBaseHandler, ILoadingHandler {
    fun showSuccess(result: SResult.Success<*>)
}