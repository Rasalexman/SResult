package com.rasalexman.sresultpresentation.base

interface IControlHandler : ILoadingHandler {
    fun onBackPressed(): Boolean
    fun onToolbarBackPressed()
    fun onNextPressed()
}