package com.rasalexman.sresultexample.emptyvm.topitems

import androidx.lifecycle.*
import com.rasalexman.easyrecyclerbinding.ScrollPosition
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresultexample.NavigationMainDirections
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.flow.filter
import java.util.*
import kotlin.random.Random

class TopItemsViewModel : BaseViewModel() {

    val scrollPosition = MutableLiveData(ScrollPosition())

    override val resultLiveData by unsafeLazy {
        asyncLiveData<ResultList<TopItemUI>> {
            emit(loadingResult())
            val random = Random.nextInt(20, 50)
            val userList = mutableListOf<TopItemUI>()
            repeat(random) {
                userList.add(
                    TopItemUI(
                        itemId = UUID.randomUUID().toString(),
                        name = UUID.randomUUID().toString().take(6),
                        color = Random.nextInt(20, 50)
                    )
                )
            }
            emit(userList.toSuccessResult())
        }
    }

    val items: LiveData<List<TopItemUI>> by unsafeLazy {
        resultLiveData.asFlow().filter { it is com.rasalexman.sresult.data.dto.SResult.Success }.asLiveData().distinctUntilChanged().map { result ->
            result.getList()
        }
    }

    fun onItemClicked(item: TopItemUI) {
        logg { "SELECTED ITEM ID = ${item.itemId}" }
        navigationLiveData.value = NavigationMainDirections.showStateFlowFragment().toNavigateResult()
    }
}