package com.rasalexman.sresultpresentation.base

interface ILoadingHandler : ISResultHandler {
    fun hideLoading()
    fun showLoading()
}