package com.rasalexman.sresultpresentation.base

import android.content.Intent
import androidx.appcompat.widget.Toolbar

interface IComplexHandler : ISResultHandler,
    IProgressHandler, INavigateHandler, ISuccessHandler,
    IFailureHandler, IToastHandler, IEmptyHandler, IControlHandler {

    fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int)

    /**
     * Adapter function for start intent with activity
     */
    fun startActivityForResult(
        intent: Intent?,
        requestCode: Int
    )
}