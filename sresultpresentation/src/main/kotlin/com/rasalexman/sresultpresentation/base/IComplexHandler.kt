package com.rasalexman.sresultpresentation.base

import android.content.Intent
import androidx.appcompat.widget.Toolbar

interface IComplexHandler :
    IControlHandler,
    IEmptyHandler,
    IFailureHandler,
    ILoadingHandler,
    INavigateHandler,
    IProgressHandler,
    ISResultHandler,
    ISuccessHandler,
    IToastHandler
     {

    fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int)

    /**
     * Adapter function for start intent with activity
     */
    fun startActivityForResult(
        intent: Intent?,
        requestCode: Int
    )
}