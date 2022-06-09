package com.rasalexman.sresultexample.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.rasalexman.sresult.common.extensions.anySuccess
import com.rasalexman.sresult.common.extensions.progressResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

class CustomDialogViewModel : BaseViewModel() {

    private val progressFormatted: String by unsafeLazy {
        string(R.string.title_loading_progress)
    }

    val title: LiveData<String> = liveData {
        emit(string(R.string.title_sustom_dialog))
    }

    private var progressCount: Int = 0

    override val resultLiveData by unsafeLazy {
        asyncLiveData<AnyResult> {
            while (progressCount < MAX_PROGRESS) {
                delay(200L)
                emit(progressResult(progressCount))
                progressCount += Random.nextInt(2, 5)
            }
            emit(anySuccess())
        }
    }

    val progress: LiveData<String> by unsafeLazy {
        resultLiveData.map {
            if(it is com.rasalexman.sresult.data.dto.SResult.Progress) {
                val progress = if (it.progress < MAX_PROGRESS) it.progress else MAX_PROGRESS
                val progressText = progressFormatted.format("$progress%")
                progressText
            } else ""
        }
    }

    companion object {
        private const val MAX_PROGRESS = 100
    }

}