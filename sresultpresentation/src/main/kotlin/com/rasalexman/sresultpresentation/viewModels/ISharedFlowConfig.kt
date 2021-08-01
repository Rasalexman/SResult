package com.rasalexman.sresultpresentation.viewModels

import kotlinx.coroutines.channels.BufferOverflow

interface ISharedFlowConfig {
    val replay: Int
    val extraBufferCapacity: Int
    val onBufferOverflow: BufferOverflow
}