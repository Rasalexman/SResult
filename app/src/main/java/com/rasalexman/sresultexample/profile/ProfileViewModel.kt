package com.rasalexman.sresultexample.profile

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel

class ProfileViewModel : BaseViewModel() {

    override val toolbarTitle: MutableLiveData<String> by unsafeLazy {
        MutableLiveData(string(R.string.title_profile))
    }

    override val toolbarSubTitle: MutableLiveData<String> by unsafeLazy {
        MutableLiveData(string(R.string.title_profile))
    }
}