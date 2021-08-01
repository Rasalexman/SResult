package com.rasalexman.sresultexample.state

import android.content.Context
import android.util.AttributeSet
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.LayoutStateFlowBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingLayout

class StateLayout : BaseBindingLayout<LayoutStateFlowBinding, StateLayoutViewModel, StateFlowFragment> {

    override val layoutId: Int
        get() = R.layout.layout_state_flow

    override val loadingLayoutResId: Int
        get() = R.id.generateLoadingLayout

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override val viewModel: StateLayoutViewModel by viewModels()
}