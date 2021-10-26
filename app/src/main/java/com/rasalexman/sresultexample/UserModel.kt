package com.rasalexman.sresultexample

import com.rasalexman.sresult.models.IConvertableTo
import com.rasalexman.sresultexample.users.UserItem
import java.util.*

data class UserModel(
    val name: String,
    val email: String,
    val token: String
) : IConvertableTo<UserItem> {
    override fun convertTo(): UserItem {
        return UserItem(
            id = UUID.randomUUID().toString(),
            firstName = name,
            lastName = email
        )
    }

}
