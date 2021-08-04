package com.rasalexman.sresultpresentation.databinding

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.rasalexman.easyrecyclerbinding.createBinding
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.layout.BaseLayout
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel

abstract class BaseBindingLayout<VB : ViewDataBinding, VM : BaseContextViewModel, F : Fragment> :
    BaseLayout<VM, F>, IBaseBindingFragment<VB, VM> {

    override var currentBinding: VB? = null
    override val binding: VB
        get() = currentBinding ?: throw NullPointerException("Binding is not initialized")

    constructor(context: Context) : super(context) {
        createBindingLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        createBindingLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        createBindingLayout(context, attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        createBindingLayout(context, attrs, defStyleAttr)
    }

    override fun createLayout(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) = Unit

    private fun createBindingLayout(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        if (!isInEditMode) {
            val inflater = LayoutInflater.from(context)
            val view = setupBindingView(inflater, this)
            applyAdditionalParameters(context, attrs, defStyleAttr, defStyleRes)
            initLayout(view)
        } else {
            inflate(context, layoutId, this)
        }
    }

    override fun setupBindingView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.createBinding<VB>(layoutId, container, attachToParent).run {
            currentBinding = this
            root
        }
    }

    override fun onAttachedToWindow() {
        if (!isInEditMode) {
            currentBinding?.let {
                it.lifecycleOwner = this
                it.setVariable(BR.vm, viewModel)
                initBinding(it)
                it.executePendingBindings()
            }
        }
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        currentBinding?.unbind()
        currentBinding = null
        super.onDetachedFromWindow()
    }

    override fun initBinding(binding: VB) = Unit
}