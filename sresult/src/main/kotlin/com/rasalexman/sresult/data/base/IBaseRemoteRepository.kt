package com.rasalexman.sresult.data.base

interface IBaseRemoteRepository<out R : IRemoteDataSource> {
    val remoteDataSource: R
}
