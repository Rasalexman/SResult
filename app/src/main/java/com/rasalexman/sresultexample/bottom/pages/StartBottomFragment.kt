package com.rasalexman.sresultexample.bottom.pages

import androidx.fragment.app.viewModels
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentStartBottomBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment

class StartBottomFragment : BaseBindingFragment<FragmentStartBottomBinding, StartBottomViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_start_bottom

    override val viewModel: StartBottomViewModel by viewModels()
}