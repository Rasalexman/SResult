package com.rasalexman.sresultexample.usecases

import com.rasalexman.kodi.annotations.BindProvider
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.typealiases.ResultList
import com.rasalexman.sresult.domain.IUseCase
import com.rasalexman.sresultexample.viewpager.pages.IRecyclerItem
import com.rasalexman.sresultexample.viewpager.pages.SimpleRecyclerItemUI
import java.util.*
import kotlin.random.Random

@BindProvider(
    toClass = ICreateItemsUseCase::class
    //toTag = "ICreateItemsUseCase"
)
internal class CreateItemsUseCase : ICreateItemsUseCase {
    override suspend fun invoke(): ResultList<IRecyclerItem> {
        val itemsList = mutableListOf<SimpleRecyclerItemUI>()
        val itemCounts = Random.nextInt(20, 100)
        repeat(itemCounts) {
            itemsList.add(SimpleRecyclerItemUI(
                title = "${it+1}). " + UUID.randomUUID().toString().take(14),
                id = Random.nextInt(100, 100000).toString()
            ))
        }
        return itemsList.toSuccessResult()
    }
}

interface ICreateItemsUseCase : IUseCase.Out<ResultList<IRecyclerItem>>