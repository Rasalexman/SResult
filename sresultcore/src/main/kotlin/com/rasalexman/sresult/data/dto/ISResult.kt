package com.rasalexman.sresult.data.dto

interface ISResult {
    fun isSuccess(): Boolean
    fun isError(): Boolean
    fun isLoading(): Boolean
    fun isEmpty(): Boolean

    fun flatMap(mapper: (ISResult) -> ISResult): ISResult

    fun<R : Any?> flatMapOnSuccess(onSuccess: (R) -> ISResult): ISResult
    fun flatMapOnError(onError: (SResult.AbstractFailure.Error) -> ISResult): ISResult
    fun flatMapOnEmpty(onEmpty: () -> ISResult): ISResult

    fun<R : Any?> applyOnSuccess(onSuccess: (R) -> Unit): ISResult
    fun applyOnError(onError: (SResult.AbstractFailure.Error) -> Unit): ISResult
    fun applyOnEmpty(onEmpty: () -> Unit): ISResult
}