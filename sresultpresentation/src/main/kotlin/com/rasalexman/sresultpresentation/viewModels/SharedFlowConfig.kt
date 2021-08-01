package com.rasalexman.sresultpresentation.viewModels

import kotlinx.coroutines.channels.BufferOverflow

data class SharedFlowConfig(
    override val replay: Int = DEFAULT_REPLAY,
    override val extraBufferCapacity: Int = DEFAULT_BUFFER,
    override val onBufferOverflow: BufferOverflow = DEFAULT_BUFFER_OVERFLOW
) : ISharedFlowConfig {
    companion object {
        private const val DEFAULT_REPLAY = 1
        private const val DEFAULT_BUFFER = 0
        private val DEFAULT_BUFFER_OVERFLOW:BufferOverflow = BufferOverflow.DROP_LATEST
    }
}
