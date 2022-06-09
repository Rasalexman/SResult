package com.rasalexman.sresultexample

import com.rasalexman.sresult.data.dto.SResult

sealed class UserAuthState : SResult.NothingResult(), IUserState {

    class UserState : UserAuthState(), IUserState
    object UserSuccessState : UserAuthState(), IUserSuccessState
    object UserFailureState : UserAuthState(), IUserFailureState

    class Success(
        userData: UserModel,
        private val userState: UserState = UserState()
    ) : SResult.Success<UserModel>(userData),
        IUserSuccessState by UserSuccessState,
        IUserState by userState

    object Failure : AbstractFailure.Failure(),
        IUserFailureState by UserFailureState,
        IUserSuccessState by UserSuccessState

}

interface IUserState
interface IUserSuccessState : IUserState
interface IUserFailureState : IUserState

