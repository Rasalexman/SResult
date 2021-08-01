package com.rasalexman.sresultpresentation.compose

import com.rasalexman.sresultpresentation.base.IBaseHandler

interface IHandlerBuilder<T : IBaseHandler> {
    fun build(): T
}