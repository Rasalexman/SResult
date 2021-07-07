package com.rasalexman.sresultexample.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.rasalexman.sresult.common.extensions.getList
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.onEvent
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.random.Random

class UsersViewModel : BaseViewModel() {

    fun onItemClicked(item: UserItem) {
        navigationLiveData.value = MainFragmentDirections.showProfileFragment(itemId = item.id, userItem = item).toNavigateResult()
    }

    init {
        processViewEvent(SEvent.Refresh)
    }

    override val resultLiveData = onEvent<SEvent.Refresh, ResultList<UserItem>>(Dispatchers.Default) {
        val random = Random.nextInt(100, 8000)
        val users: MutableList<UserItem> = mutableListOf()
        repeat(random) {
            users.add(
                UserItem(
                    id = UUID.randomUUID().toString(),
                    firstName = UUID.randomUUID().toString().take(4),
                    lastName = UUID.randomUUID().toString().takeLast(6)
                ))
        }
        emit(users.toSuccessResult())
    }

    val items: LiveData<List<UserItem>> by unsafeLazy {
        resultLiveData.switchMap { result ->
            asyncLiveData(Dispatchers.Default) {
                emit(result.getList())
            }
        }
    }
}