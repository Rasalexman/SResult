@file:Suppress("unused")
package com.rasalexman.sresultpresentation.extensions

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.emptyResult
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresult.models.IConvertableTo

typealias AnyResultLiveData = LiveData<AnyResult>
typealias AnyResultMutableLiveData = MutableLiveData<AnyResult>
typealias ResultLiveData<T> = LiveData<com.rasalexman.sresult.data.dto.SResult<T>>
typealias ResultListLiveData<T> = LiveData<com.rasalexman.sresult.data.dto.SResult<List<T>>>
typealias ResultMutableLiveData<T> = MutableLiveData<com.rasalexman.sresult.data.dto.SResult<T>>

@Suppress("UNCHECKED_CAST")
inline fun <X, Y> LiveData<X>.mutableMap(
    defaultValue: Y? = null,
    crossinline transform: (X) -> Y
): MutableLiveData<Y> = (this.map(transform) as MutableLiveData<Y>).apply {
    defaultValue?.let { this.value = it }
}

inline fun <X, Y> LiveData<X>.mutableSwitchMap(
    defaultValue: Y? = null,
    crossinline transform: (X) -> LiveData<Y>
): MutableLiveData<Y> = (this.switchMap(transform) as MutableLiveData<Y>).apply {
    defaultValue?.let { this.value = it }
}

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

fun <X, Y> LiveData<X>.mapWith(func: (X?) -> Y?): MutableLiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@mapWith) { x -> value = func(x) }
    }
}

fun <X, Y> LiveData<X>.mapImmediately(func: (X?) -> Y?): MutableLiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@mapImmediately) { x -> value = func(x) }
        value = func(this@mapImmediately.value)
    }
}

fun <X, Y> LiveData<X>.mapSkipNulls(func: (X) -> Y): MutableLiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@mapSkipNulls) { x ->
            x ?: return@addSource
            value = func(x)
        }
    }
}

fun <A, B> LiveData<A>.combineLatest(b: LiveData<B>): LiveData<Pair<A?, B?>> {
    return MediatorLiveData<Pair<A?, B?>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        addSource(this@combineLatest) {
            lastA = it
            value = lastA to lastB
        }

        addSource(b) {
            lastB = it
            value = lastA to lastB
        }
    }
}

fun <A> MutableLiveData<A?>.executeWhenChanged(f: (A?) -> Unit): MutableLiveData<A?> {
    return MediatorLiveData<A?>().apply {
        addSource(this@executeWhenChanged) {
            value = it
            f(it)
        }
    }
}

fun MutableLiveData<Boolean>.toggle() {
    val currentValue = value ?: return
    postValue(!currentValue)
}

fun <T> LiveData<T>.debounce(duration: Long = 500L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = Handler(Looper.getMainLooper())

    val runnable = Runnable {
        mld.value = source.value
    }

    mld.addSource(source) {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, duration)
    }
}

fun <T : Any> LiveData<ResultList<T>>?.getList(): List<T> {
    return this?.value?.data.orEmpty()
}

fun <T : Any> LiveData<com.rasalexman.sresult.data.dto.SResult<T>>?.postResultValue(result: com.rasalexman.sresult.data.dto.SResult<T>): com.rasalexman.sresult.data.dto.SResult<T> {
    (this as? MutableLiveData<com.rasalexman.sresult.data.dto.SResult<T>>)?.postValue(result)
    return result
}

fun <T : Any> LiveData<ResultList<T>>?.postListValue(result: List<T>): ResultList<T> {
    val newResultList = result.toSuccessResult()
    (this as? MutableLiveData<ResultList<T>>)?.postValue(newResultList)
    return newResultList
}

fun <T : Any> LiveData<ResultList<T>>?.addListValue(result: List<T>): ResultList<T> {
    val oldValue = getList()
    val newList = result + oldValue
    val newResultList = newList.toSuccessResult()
    (this as? MutableLiveData<ResultList<T>>)?.postValue(newResultList)
    return newResultList
}

fun LiveData<*>?.clear() {
    (this as? MutableLiveData<*>)?.value = null
}
