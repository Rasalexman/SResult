package com.rasalexman.sresultexample.users

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserItem(
    val id: String,
    val firstName: String,
    val lastName: String
) : Parcelable {

    @IgnoredOnParcel
    val fullName: String
        get() = "$firstName $lastName"
}
