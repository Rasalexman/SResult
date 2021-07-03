package com.rasalexman.sresultexample.profile

import android.content.Context
import android.util.AttributeSet
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemFilterDropdownBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingLayout

class ProfileLayout : BaseBindingLayout<ItemFilterDropdownBinding, ProfileLayoutViewModel, ProfileFragment> {

    override val layoutId: Int
        get() = R.layout.item_filter_dropdown

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override val viewModel: ProfileLayoutViewModel by viewModels()

    override fun initBinding(binding: ItemFilterDropdownBinding) {
        binding.dropdownItems = viewModel.generatedItems
        binding.selectedItem = viewModel.selectedItem
    }
}