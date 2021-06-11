package com.rasalexman.sresultexample

import com.rasalexman.sresult.data.dto.SResult

sealed class UserAuthState : SResult.NothingResult(), IUserState {

    class UserState : UserAuthState(), IUserState
    object UserSuccessState : UserAuthState(), IUserSuccessState
    object UserFailureState : UserAuthState(), IUserFailureState

    data class Success(
        val token: String,
        val userState: UserState = UserState()
    ) : SResult.EmptySuccess(),
        IUserSuccessState by UserSuccessState,
        IUserState by userState

    object Failure : SResult.AbstractFailure.Failure(),
        IUserFailureState by UserFailureState,
        IUserSuccessState by UserSuccessState

}

interface IUserState
interface IUserSuccessState : IUserState
interface IUserFailureState : IUserState

