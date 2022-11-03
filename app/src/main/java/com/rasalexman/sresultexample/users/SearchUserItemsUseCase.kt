package com.rasalexman.sresultexample.users

import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.common.extensions.mapFlowListTo
import com.rasalexman.sresult.common.extensions.orIfNull
import com.rasalexman.sresult.common.extensions.toSuccessResult
import com.rasalexman.sresult.common.typealiases.FlowResultList
import com.rasalexman.sresult.domain.IUseCase
import com.rasalexman.sresultexample.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import kotlin.random.Random

internal class SearchUserItemsUseCase : ISearchUserItemsUseCase, ICoroutinesManager {

    override suspend fun invoke(data: Flow<String>): FlowResultList<UserItem> {
        return createItems().combine(data) { items, query ->
            val filtered = items.takeIf { query.isEmpty() }.orIfNull {
                items.filter { it.name.contains(query, true) }
            }
            logg { "query = $query | filtered size = ${filtered.size}" }
            filtered.toSuccessResult()
        }.mapFlowListTo()
    }

    private fun createItems(): Flow<List<UserModel>> {
        return flow {
            val random = Random.nextInt(100, 8000)
            val userList = mutableListOf<UserModel>()
            repeat(random) {
                userList.add(
                    UserModel(
                        token = UUID.randomUUID().toString(),
                        name = UUID.randomUUID().toString().take(4),
                        email = UUID.randomUUID().toString().takeLast(6)
                    )
                )
            }
            emit(userList)
        }.flowOn(Dispatchers.Default)
    }
}

interface ISearchUserItemsUseCase : IUseCase.SingleInOut<Flow<String>, FlowResultList<UserItem>>