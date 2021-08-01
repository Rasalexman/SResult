package com.rasalexman.sresultpresentation.databinding

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.rasalexman.easyrecyclerbinding.createBinding
import com.rasalexman.easyrecyclerbinding.getOwner
import com.rasalexman.sresult.common.extensions.applyIf
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.BR
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.layout.BaseLayout
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.BaseViewModel
import com.rasalexman.sresultpresentation.viewModels.CustomViewModelLazy
import java.lang.ref.WeakReference

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
        val inflater = LayoutInflater.from(context)
        val view = setupBindingView(inflater, this)
        applyAdditionalParameters(context, attrs, defStyleAttr, defStyleRes)
        initLayout(view)
    }

    override fun setupBindingView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.createBinding<VB>(layoutId, container, attachToParent).run {
            currentBinding = this
            root
        }
    }

    override fun onAttachedToWindow() {
        currentBinding?.let {
            it.lifecycleOwner = this
            it.setVariable(BR.vm, viewModel)
            initBinding(it)
            it.executePendingBindings()
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