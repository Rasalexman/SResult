package com.rasalexman.sresultpresentation.base

interface IControlHandler : ISResultHandler {
    fun onBackPressed(): Boolean
    fun onToolbarBackPressed()
    fun onNextPressed()
}