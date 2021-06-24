package com.rasalexman.sresultexample.users

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.getList
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import java.util.*
import kotlin.random.Random

class UsersViewModel : BaseViewModel() {
    fun onItemClicked(item: UserItem) {
        navigationLiveData.value = MainFragmentDirections.showProfileFragment(itemId = item.id, userItem = item).toNavigateResult()
    }

    override val resultLiveData = asyncLiveData<ResultList<UserItem>> {
        val random = Random.nextInt(10, 50)
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

    val items: MutableLiveData<List<UserItem>> = resultLiveData.mutableMap {
        it.getList()
    }
}