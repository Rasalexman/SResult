package com.rasalexman.sresultexample.emptyvm.topitems

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.rasalexman.easyrecyclerbinding.recyclerConfig
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.ItemTopBinding
import com.rasalexman.sresultexample.databinding.LayoutTopItemsBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingLayout

class TopItemsLayout : BaseBindingLayout<LayoutTopItemsBinding, TopItemsViewModel> {
    override val layoutId: Int
        get() = R.layout.layout_top_items

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override val viewModel: TopItemsViewModel by viewModels()

    override fun initBinding(binding: LayoutTopItemsBinding) {
        super.initBinding(binding)
        binding.rvConfig = recyclerConfig<TopItemUI, ItemTopBinding> {
            itemId = BR.item
            layoutId = R.layout.item_top
            hasFixedSize = false
            orientation = LinearLayout.HORIZONTAL
            onItemClick = { item, _ ->
                viewModel.onItemClicked(item)
            }

        }
    }
}