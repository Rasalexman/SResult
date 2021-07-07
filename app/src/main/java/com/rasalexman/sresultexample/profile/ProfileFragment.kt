package com.rasalexman.sresultexample.profile

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentProfileBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment

class ProfileFragment : BaseBindingFragment<FragmentProfileBinding, ProfileViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_profile

    override val viewModel: ProfileViewModel by viewModels()

    override val needBackButton: Boolean
        get() = true

    override fun onBackArgumentsHandler(backArgs: Bundle) {
        println("------> backArgs = $backArgs")
    }
}