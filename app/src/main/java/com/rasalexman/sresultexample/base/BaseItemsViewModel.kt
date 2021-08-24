package com.rasalexman.sresultexample.base

import androidx.lifecycle.*
import com.rasalexman.sresult.common.extensions.getList
import com.rasalexman.sresult.common.extensions.loadingResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.ISEvent
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.users.UserItem
import com.rasalexman.sresultpresentation.extensions.onEvent
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import java.util.*
import kotlin.random.Random

abstract class BaseItemsViewModel : BaseViewModel() {

    override val eventLiveData: MutableLiveData<ISEvent> = MutableLiveData<ISEvent>(SEvent.Refresh)

    override val resultLiveData = onEvent<SEvent.Refresh, ResultList<UserItem>>(Dispatchers.Default) {
        emit(loadingResult())
        val random = Random.nextInt(100, 8000)
        val users: MutableList<UserItem> = mutableListOf()
        repeat(random) {
            users.add(
                UserItem(
                    id = UUID.randomUUID().toString(),
                    firstName = UUID.randomUUID().toString().take(4),
                    lastName = UUID.randomUUID().toString().takeLast(6)
                )
            )
        }
        emit(users.toSuccessResult())
    }

   open val items: LiveData<List<UserItem>> by unsafeLazy {
        resultLiveData.asFlow().filter { it is SResult.Success }.asLiveData().map { result ->
            result.getList()
        }
    }
}