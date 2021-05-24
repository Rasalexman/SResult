package com.rasalexman.sresult.data.base

interface IBaseLocalRepository<out L : ILocalDataSource> {
    val localDataSource: L
}
