package com.rasalexman.sresultexample.bottomrecycler

import com.rasalexman.sresultexample.base.BaseItemsViewModel

class BottomRecyclerViewModel : BaseItemsViewModel() {
    fun onShowEmpty() {
        onBackClicked()
    }


}