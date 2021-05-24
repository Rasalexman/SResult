package com.rasalexman.sresultpresentation.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rasalexman.sresultpresentation.tools.startProgressTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class CoreLoadingViewModel : BaseViewModel(), ICoreLoadingViewModel {
    var progressJob: Job? = null
    override val progress: MutableLiveData<Boolean> = MutableLiveData(false)
    override val elapsedTime: MutableLiveData<Long> = MutableLiveData()

    /*
     * Если в elapsedTime стоит с значение установленным по умолчанию (0L), то TextView который
     * байндит себе от нее значения с адаптерами:
     * app:timeFormat="@{DateFormat.TIME_FORMAT_mmss}"
     * app:unixTime="@{vm.elapsedTime}"
     * Нельзя будет менять видимость (адаптер делает текствью видимым если в лайвдате есть значение)
     * */
    override fun showProgress() {
        elapsedTime.value = 0L
        progressJob = viewModelScope.launch {
            startProgressTimer(
                coroutineScope = this,
                elapsedTime = elapsedTime,
                hideProgress = ::hideProgress
            )
        }
    }

    override fun hideProgress() {
        clean()
        progressJob?.cancel()
    }

    override fun clean() {
        progress.postValue(false)
        elapsedTime.postValue(-1L)
    }
}