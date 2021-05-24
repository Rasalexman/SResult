package com.rasalexman.sresult.data.base

interface IBaseRepository<out L : ILocalDataSource, out R : IRemoteDataSource> :
    IBaseLocalRepository<L>, IBaseRemoteRepository<R>
