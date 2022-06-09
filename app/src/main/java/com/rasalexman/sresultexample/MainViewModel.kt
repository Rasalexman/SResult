package com.rasalexman.sresultexample

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.*
import com.rasalexman.sresult.models.IDropDownItem
import com.rasalexman.sresultpresentation.extensions.AnyResultMutableLiveData
import com.rasalexman.sresultpresentation.extensions.mutableMap
import com.rasalexman.sresultpresentation.extensions.onEventLiveDataAnyResult
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emitAll
import java.util.*
import kotlin.random.Random

class MainViewModel : BaseViewModel() {

    data class Item(
        override val itemId: String,
        override val title: String
    ) : IDropDownItem

    companion object {
        private const val PROGRESS_DELAY = 1200L
    }

    val items by unsafeLazy {
        MutableLiveData<List<IDropDownItem>>(
            listOf(Item("11", "Hello"), Item("112", "World"))
        )
    }

    val items2 by unsafeLazy {
        MutableLiveData<List<IDropDownItem>>(
            listOf(
                Item("121", "Full text"),
                Item("1212", "Abracadabra"),
                Item("122", "Simple Text")
            )
        )
    }

    val selectedValue by unsafeLazy {
        items.mutableMap {
            it.first()
        }
    }

    val selectedValue2 by unsafeLazy {
        items2.mutableMap {
            it.first()
        }
    }

    override val toolbarMenu: MutableLiveData<Int> = MutableLiveData(R.menu.menu_add)

    override val resultLiveData = onEventLiveDataAnyResult<com.rasalexman.sresult.data.dto.SEvent.Fetch>(Dispatchers.Default, isDistinct = true) {
        logg { "resultLiveData event is $it" }

        emit(loadingResult())

        emit(progressResult(10))
        delay(PROGRESS_DELAY)

        emit(progressResult(50))
        delay(PROGRESS_DELAY)
        val user = UserModel(
            name = "Alex",
            email = "sphc@yandex.ru",
            token = UUID.randomUUID().toString()
        )
        emit(progressResult(70))

        val result = getAnotherResult()

        emitAll(safeIoResultFlow<Int> {
            emit(result)
        })

        emit(progressResult(90))
        delay(PROGRESS_DELAY)

        emit(UserAuthState.Success(user))
    }

    private suspend fun getAnotherResult(): com.rasalexman.sresult.data.dto.SResult<Int> {
        return getResult().flatMapIfDataTypedSuspend<UserModel, Int> {
            1.toSuccessResult()
        }.flatMapIfSuccessTyped<Int, Int> {
            9.toSuccessResult()
        }
    }

    override val supportLiveData = onEventLiveDataAnyResult<com.rasalexman.sresult.data.dto.SEvent.Refresh>(
        dispatcher = Dispatchers.Default,
        isDistinct = true
    ) {
        logg { "supportLiveData event is $it" }
        val rand = Random.nextInt(10, 54)
        emit(progressResult(rand))
    } as AnyResultMutableLiveData

    fun onGenerateClicked() {
        val rand = Random.nextInt(10, 54)
        val event: com.rasalexman.sresult.data.dto.ISEvent = if(rand % 2 == 0) com.rasalexman.sresult.data.dto.SEvent.Fetch else com.rasalexman.sresult.data.dto.SEvent.Refresh
        processEvent(event)
    }

    private fun getResult(): com.rasalexman.sresult.data.dto.SResult<UserModel> {
        return UserModel(
            name = "Alex",
            email = "sphc@yandex.ru",
            token = UUID.randomUUID().toString()
        ).toSuccessResult()
    }

    fun onUsersClicked() {
        navigationLiveData.value = NavigationMainDirections.showUsersFragment(
            itemId = "HELLO WORLD"
        ).toNavigateResult()
    }

    fun onStateButtonClicked() {
        navigationLiveData.value = NavigationMainDirections.showStateFlowFragment().toNavigateResult()
    }

    fun onMenuChangeClicked() {
        val menuResId = if(toolbarMenu.value == R.menu.menu_add) {
            R.menu.menu_add_disabled
        } else {
            R.menu.menu_add
        }
        toolbarMenu.value = menuResId
    }
}