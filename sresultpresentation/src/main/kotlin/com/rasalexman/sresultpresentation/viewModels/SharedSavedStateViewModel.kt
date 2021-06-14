package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.rasalexman.kodi.core.*
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch

abstract class SharedSavedStateViewModel(savedState: SavedStateHandle? = null) : BaseViewModel() {

    private val sharedTag: String by unsafeLazy {
        "${this@SharedSavedStateViewModel.hashCode()}"
    }

    private val sharedSavedStateHandler: SavedStateHandle by immutableInstance()
    protected open val savedStateHandler: SavedStateHandle = savedState?.let(::bindSharedState).or {
        if(hasInstance<SavedStateHandle>()) sharedSavedStateHandler else {
            bindSharedState(SavedStateHandle())
        }
    }

    /**
     * Need to clear all data saved in [SavedStateHandle]
     */
    protected open val isNeedToClear: Boolean = false

    /**
     * Base Function to add to ViewStateHandler Some Values by key [String]
     */
    fun <T : Any> SharedSavedStateViewModel.addToSavedState(key: String, value: T) {
        savedStateHandler.set(key, value)
    }

    /**
     * Base Function fro getting value from ViewStateHandler by key [String]
     */
    fun <T : Any> SharedSavedStateViewModel.getFromSavedState(key: String): T? {
        return savedStateHandler[key]
    }

    /**
     * Remove data by key
     */
    fun SharedSavedStateViewModel.removeFromSavedState(key: String) {
        savedStateHandler.remove<Any>(key)
    }

    /**
     * Get live data for key
     */
    fun <T : Any> SharedSavedStateViewModel.getLiveSavedState(key: String, initialValue: T? = null): MutableLiveData<T?> {
        return savedStateHandler.getLiveData(key, initialValue)
    }

    override fun onCleared() {
        if(isNeedToClear) {
            clearAllSavedStates()
        }
        unbindSharedState()
        super.onCleared()
    }

    private fun bindSharedState(savedState: SavedStateHandle): SavedStateHandle {
        unbindSharedState()
        bind<SavedStateHandle>() with single { savedState }
        bindTag(sharedTag) with single { sharedTag }
        return savedState
    }

    private fun unbindSharedState() {
        if(hasInstance<SavedStateHandle>(sharedTag)) {
            unbindTag(sharedTag)
            unbind<SavedStateHandle>()
        }
    }

    protected open fun clearAllSavedStates() = launchUITryCatch {
        savedStateHandler.keys().forEach {
            removeFromSavedState(it)
        }
    }
}