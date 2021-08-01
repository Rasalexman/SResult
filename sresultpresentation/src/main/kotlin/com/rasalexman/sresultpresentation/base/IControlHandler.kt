package com.rasalexman.sresultpresentation.base

interface IControlHandler : IProgressHandler {
    fun onBackPressed(): Boolean
    fun onToolbarBackPressed()
    fun onNextPressed()
}