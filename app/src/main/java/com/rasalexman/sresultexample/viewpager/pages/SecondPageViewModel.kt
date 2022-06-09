package com.rasalexman.sresultexample.viewpager.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.easyrecyclerbinding.IBindingModel
import com.rasalexman.easyrecyclerbinding.ScrollPosition
import com.rasalexman.easyrecyclerbinding.recyclerConfig
import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.common.typealiases.AnyResult
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.NavigationMainDirections
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemRecyclerBinding
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.extensions.onEvent
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import java.util.*
import kotlin.random.Random

class SecondPageViewModel : BaseViewModel(), IBindingModel {
    override val layoutResId: Int
        get() = R.layout.item_vp2_second_page

    val scrollPosition = MutableLiveData(ScrollPosition())

    val items: LiveData<MutableList<IRecyclerItem>> by unsafeLazy {
        asyncLiveData {
            val existedList = mutableListOf<IRecyclerItem>()
            val itemsList = mutableListOf<IRecyclerItem>()
            val itemCounts = Random.nextInt(20, 100)
            repeat(itemCounts) {
                itemsList.add(itemsCreator(it))
            }
            existedList.addAll(itemsList)
            emit(existedList)
        }
    }

    override val resultLiveData = onEvent<OnShowUserEvent, AnyResult>() {
        emit( NavigationMainDirections.showUsersFragment(
            itemId = "HELLO WORLD"
        ).toNavigateResult())
    }

    fun createRvConfig() = recyclerConfig<SimpleRecyclerItemUI, ItemRecyclerBinding> {
        itemId = BR.item
        layoutId = R.layout.item_recycler
        onItemClick = { _ ->
            showProfileUsersFragment()
        }
    }

    private fun showProfileUsersFragment() {
        processEvent(OnShowUserEvent())
    }

    private fun itemsCreator(position: Int): IRecyclerItem {
        return SimpleRecyclerItemUI(
            title = "$position). " + UUID.randomUUID().toString().take(14),
            id = Random.nextInt(100, 100000).toString()
        )
    }

    class OnShowUserEvent : com.rasalexman.sresult.data.dto.ISEvent

}