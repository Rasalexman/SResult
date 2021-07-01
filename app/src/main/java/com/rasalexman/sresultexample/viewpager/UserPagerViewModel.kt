package com.rasalexman.sresultexample.viewpager

import androidx.lifecycle.MutableLiveData
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultpresentation.viewModels.BaseViewPagerViewModel

class UserPagerViewModel : BaseViewPagerViewModel() {

    override val toolbarTitle: MutableLiveData<String> by unsafeLazy {
        MutableLiveData(string(R.string.button_view_pager))
    }
    override val selectedPage: MutableLiveData<Int> = MutableLiveData(1)
}