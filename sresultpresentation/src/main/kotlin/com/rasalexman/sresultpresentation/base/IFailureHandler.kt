package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface IFailureHandler : IProgressHandler {

    /**
     * When [SResult.AbstractFailure.Error] is coming
     */
    fun showFailure(error: SResult.AbstractFailure.Failure)

    /**
     * When [SResult.AbstractFailure.Alert] is coming
     */
    fun showAlert(alert: SResult.AbstractFailure.Alert)
}