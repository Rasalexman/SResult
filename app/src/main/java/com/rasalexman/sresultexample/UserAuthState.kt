package com.rasalexman.sresultexample

sealed class UserAuthState : com.rasalexman.sresult.data.dto.SResult.NothingResult(), IUserState {

    class UserState : UserAuthState(), IUserState
    object UserSuccessState : UserAuthState(), IUserSuccessState
    object UserFailureState : UserAuthState(), IUserFailureState

    class Success(
        userData: UserModel,
        private val userState: UserState = UserState()
    ) : com.rasalexman.sresult.data.dto.SResult.Success<UserModel>(userData),
        IUserSuccessState by UserSuccessState,
        IUserState by userState

    object Failure : com.rasalexman.sresult.data.dto.SResult.AbstractFailure.Failure(),
        IUserFailureState by UserFailureState,
        IUserSuccessState by UserSuccessState

}

interface IUserState
interface IUserSuccessState : IUserState
interface IUserFailureState : IUserState

