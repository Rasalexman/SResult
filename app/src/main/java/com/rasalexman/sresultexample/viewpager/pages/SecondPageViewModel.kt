package com.rasalexman.sresultexample.viewpager.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasalexman.easyrecyclerbinding.recyclerConfig
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemRecyclerBinding
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.viewModels.BasePageViewModel
import java.util.*
import kotlin.random.Random

class SecondPageViewModel : BasePageViewModel() {
    override val layoutResId: Int
        get() = R.layout.item_vp2_second_page

    val items: LiveData<MutableList<IRecyclerItem>> by unsafeLazy {
        asyncLiveData<MutableList<IRecyclerItem>> {
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

    fun createRvConfig() = recyclerConfig<SimpleRecyclerItemUI, ItemRecyclerBinding> {
        itemId = BR.item
        layoutId = R.layout.item_recycler
    }

    private fun itemsCreator(position: Int): IRecyclerItem {
        return SimpleRecyclerItemUI(
            title = "$position). " + UUID.randomUUID().toString().take(14),
            id = Random.nextInt(100, 100000).toString()
        )
    }

}