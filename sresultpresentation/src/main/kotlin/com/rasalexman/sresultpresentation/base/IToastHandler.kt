package com.rasalexman.sresultpresentation.base

import com.rasalexman.sresult.data.dto.SResult

interface IToastHandler : ISResultHandler {

    /**
     * When [SResult.Toast] is coming
     */
    fun showToast(message: Any?, interval: Int)
}