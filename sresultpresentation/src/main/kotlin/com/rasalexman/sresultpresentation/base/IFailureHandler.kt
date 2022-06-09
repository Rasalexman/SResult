package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface IFailureHandler : ILoadingHandler {

    /**
     * When [SResult.AbstractFailure.Error] is coming
     */
    fun showFailure(error: com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Failure)

    /**
     * When [SResult.AbstractFailure.Alert] is coming
     */
    fun showAlert(alert: com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Alert)
}