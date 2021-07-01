package com.rasalexman.sresultpresentation.viewModels

import com.rasalexman.easyrecyclerbinding.IBindingModel

abstract class BasePageViewModel : BaseViewModel(), IBindingModel {

    open fun clearPage() {
        onCleared()
    }
}