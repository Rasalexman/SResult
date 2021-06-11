package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface IProgressHandler : ILoadingHandler {
    /**
     * Update progress from [SResult.Progress] state
     */
    fun showProgress(progress: Int, message: Any?)
}