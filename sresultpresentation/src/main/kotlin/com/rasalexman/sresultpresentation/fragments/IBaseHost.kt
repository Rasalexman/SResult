package com.rasalexman.sresultpresentation.fragments

interface IBaseHost {
    val currentNavHandler: INavigationHandler?
    val navControllerId: Int
    val navigatorTag: String

    fun unbindNavController()
    fun bindNavController()
}