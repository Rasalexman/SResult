package com.rasalexman.sresultexample.viewpager.pages

import androidx.lifecycle.MutableLiveData
import com.rasalexman.easyrecyclerbinding.IBindingModel
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class FirstPageViewModel : BaseViewModel(), IBindingModel {

    val login: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()


    fun onSingInButtonClicked() {
        println("------> onSingInButtonClicked login = ${login.value} password = ${password.value}")
    }

    override val layoutResId: Int
        get() = R.layout.item_vp2_first_page
}