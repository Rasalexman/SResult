package com.rasalexman.sresultexample.bottom.pages

import androidx.fragment.app.viewModels
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentNextBottomBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment

class NextBottomFragment : BaseBindingFragment<FragmentNextBottomBinding, NextBottomViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_next_bottom

    override val viewModel: NextBottomViewModel by viewModels()
}