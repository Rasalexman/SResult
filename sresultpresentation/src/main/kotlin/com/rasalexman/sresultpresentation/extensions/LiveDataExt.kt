package com.rasalexman.sresultpresentation.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.rasalexman.sresult.common.extensions.emptyResult
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.models.IConvertableTo

@Suppress("UNCHECKED_CAST")
inline fun <X, Y> LiveData<X>.mutableMap(
    crossinline transform: (X) -> Y
): MutableLiveData<Y> = this.map(transform) as MutableLiveData<Y>

inline fun <X, Y> LiveData<X>.mutableSwitchMap(
    crossinline transform: (X) -> LiveData<Y>
): MutableLiveData<Y> = this.switchMap(transform) as MutableLiveData<Y>

fun <X : IConvertableTo<Y>, Y> LiveData<X>.mapTo() {
    this.map {
        it.convertTo()
    }
}

fun MutableLiveData<Boolean>.reverse() {
    try {
        value?.let { value = !it }
    } catch (e: IllegalStateException) {
        loggE(e, e.message)
    }
}

fun MutableLiveData<Boolean>.reverseAsync() {
    this.value?.let {
        postValue(!it)
    }
}

fun LiveData<AnyResult>.setupAsEmpty() {
    (this as? MutableLiveData<AnyResult>)?.value = emptyResult()
}