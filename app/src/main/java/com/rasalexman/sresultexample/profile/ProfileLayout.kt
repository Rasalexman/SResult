package com.rasalexman.sresultexample.profile

import android.content.Context
import android.util.AttributeSet
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemFilterDropdownBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingLayout

class ProfileLayout constructor(context: Context, attrs: AttributeSet?) : BaseBindingLayout<ItemFilterDropdownBinding, ProfileLayoutViewModel, ProfileFragment>(context, attrs) {

    override val layoutId: Int
        get() = R.layout.item_filter_dropdown

    override val viewModel: ProfileLayoutViewModel by viewModels()

    override fun initBinding(binding: ItemFilterDropdownBinding) {
        binding.dropdownItems = viewModel.generatedItems
        binding.selectedItem = viewModel.selectedItem
    }
}