package com.rasalexman.sresult.data.dto

sealed class SEvent : ISEvent {

    /**
     * Fetch data from ViewModel
     */
    object Fetch : SEvent()

    /**
     * Refresh data
     */
    object Refresh : SEvent()

    /**
     * Validate Any data with ViewModel
     */
    object Validate : SEvent()

    /**
     * Try Again
     */
    object TryAgain : SEvent()

    /**
     * Fetch data with input params
     */
    data class FetchWith<T : Any>(val params: T) : SEvent()
}