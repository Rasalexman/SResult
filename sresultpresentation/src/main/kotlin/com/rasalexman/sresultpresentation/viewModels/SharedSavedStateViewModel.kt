package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresultpresentation.extensions.launchUITryCatch

abstract class SharedSavedStateViewModel(savedState: SavedStateHandle? = null) : BaseViewModel() {

    protected open val sharedSavedStateHandler: SavedStateHandle by immutableInstance()
    protected open val savedStateHandler: SavedStateHandle by lazy { savedState ?: sharedSavedStateHandler }

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
        super.onCleared()
    }

    protected open fun clearAllSavedStates() = launchUITryCatch {
        savedStateHandler.keys().forEach {
            removeFromSavedState(it)
        }
    }
}