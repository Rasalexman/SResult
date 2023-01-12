package com.rasalexman.sresultexample

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.rasalexman.sresult.common.extensions.applyIfSuccessTyped
import com.rasalexman.sresult.common.extensions.applyIfType
import com.rasalexman.sresult.common.extensions.logg
import com.rasalexman.sresult.common.extensions.unsafeLazy
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultexample.databinding.FragmentMainBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment
import com.rasalexman.sresultpresentation.extensions.string

class MainFragment : BaseBindingFragment<FragmentMainBinding, MainViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_main

    override val toolbarTitleResId: Int
        get() = R.string.title_main

    override val viewModel: MainViewModel by viewModels()

    private val progressText by unsafeLazy {
        string(R.string.title_progress)
    }

    override fun onBackArgumentsHandler(backArgs: Bundle) {
        println("------> backArgs = $backArgs")
    }

    override fun onResultHandler(result: SResult<*>) {
        super.onResultHandler(result)
        result.applyIfSuccessTyped<UserModel> {
            logg { "-----> applyIfSuccessTyped = $this" }
            binding.progressTV.text = it.token
        }.applyIfType<SResult.Progress> {
            binding.progressTV.text = buildString {
                append(progressText.format("$progress"))
                append("%")
            }
        }
    }

}