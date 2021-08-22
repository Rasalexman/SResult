package com.rasalexman.sresultexample.viewpager.pages

import androidx.lifecycle.LiveData
import com.rasalexman.easyrecyclerbinding.IBindingModel
import com.rasalexman.easyrecyclerbinding.recyclerConfig
import com.rasalexman.kodi.core.immutableInstance
import com.rasalexman.sresult.common.extensions.getList
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemRecyclerBinding
import com.rasalexman.sresultexample.usecases.ICreateItemsUseCase
import com.rasalexman.sresultpresentation.extensions.asyncLiveData
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class SecondPageViewModel : BaseViewModel(), IBindingModel {
    override val layoutResId: Int
        get() = R.layout.item_vp2_second_page

    private val createItemsUseCase: ICreateItemsUseCase by immutableInstance()

    val items: LiveData<List<IRecyclerItem>> by unsafeLazy {
        asyncLiveData {
            emit(createItemsUseCase().getList())
        }
    }

    fun createRvConfig() = recyclerConfig<SimpleRecyclerItemUI, ItemRecyclerBinding> {
        itemId = BR.item
        layoutId = R.layout.item_recycler
    }

    /*private fun itemsCreator(position: Int): IRecyclerItem {
        return SimpleRecyclerItemUI(
            title = "$position). " + UUID.randomUUID().toString().take(14),
            id = Random.nextInt(100, 100000).toString()
        )
    }*/

}