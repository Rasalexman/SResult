package com.rasalexman.sresultpresentation.fragments

import androidx.navigation.NavController

interface INavigationHandler {
    val currentNavHandler: INavigationHandler?
    val navController: NavController?
    fun onSupportNavigateUp(): Boolean
    fun onBackPressed(): Boolean
}