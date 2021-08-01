package com.rasalexman.sresultpresentation.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.rasalexman.sresultpresentation.viewModels.IBaseViewModel

abstract class BaseHostFragment<VM : IBaseViewModel> : BaseFragment<VM>(), IBaseHost {

    override val navController: NavController
        get() = Navigation.findNavController(
            requireActivity(),
            navControllerId
        )

    override val loadingViewLayout: View?
        get() = null

    override val contentViewLayout: View?
        get() = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unbindNavController()
        bindNavController()
    }

    override fun onDestroyView() {
        unbindNavController()
        super.onDestroyView()
    }
}
