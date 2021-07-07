package com.rasalexman.sresultexample.bottom

import androidx.fragment.app.viewModels
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentUserBottomBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingBottomDialogFragment

class UserBottomDialogFragment : BaseBindingBottomDialogFragment<FragmentUserBottomBinding, UserBottomViewModel>() {

    override val layoutId: Int
        get() = R.layout.fragment_user_bottom

    override val viewModel: UserBottomViewModel by viewModels()
}