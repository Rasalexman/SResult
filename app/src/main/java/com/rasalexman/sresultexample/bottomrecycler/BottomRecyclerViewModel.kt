package com.rasalexman.sresultexample.bottomrecycler

import com.rasalexman.sresult.common.extensions.toNavigateResult
import com.rasalexman.sresultexample.MainFragmentDirections
import com.rasalexman.sresultexample.base.BaseItemsViewModel

class BottomRecyclerViewModel : BaseItemsViewModel() {
    fun onShowEmpty() {
        navigationLiveData.value = MainFragmentDirections.showEmptyFragment().toNavigateResult()
    }


}