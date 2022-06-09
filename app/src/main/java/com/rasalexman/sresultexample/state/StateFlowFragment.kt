package com.rasalexman.sresultexample.state

import androidx.fragment.app.viewModels
import com.rasalexman.sresult.data.dto.SEvent
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentStateFlowBinding
import com.rasalexman.sresultpresentation.databinding.BaseBindingFragment

class StateFlowFragment : BaseBindingFragment<FragmentStateFlowBinding, StateFlowViewModel>() {

    override val layoutId: Int
        get() = R.layout.fragment_state_flow

    override val needBackButton: Boolean
        get() = true

    override val viewModel: StateFlowViewModel by viewModels()

    override fun onAnyDataHandler(data: Any?) {
        println("-----> onAnyDataHandler = $data")
        processViewEvent(SEvent.Fetch)
    }
}