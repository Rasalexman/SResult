package com.rasalexman.sresult.data.dto

interface ISResult<out T : Any> {
    /**
     * This is a main data handle variable
     */
    val data: T?

    /**
     * This is flag for presentation layer to said need handle this result
     */
    var isNeedHandle: Boolean

    /**
     * This flag told does it already handled
     */
    var isHandled: Boolean
}