package com.rasalexman.sresultpresentation.layout

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.rasalexman.easyrecyclerbinding.findPrimaryFragment
import com.rasalexman.easyrecyclerbinding.getOwner
import com.rasalexman.sresult.common.extensions.getMessage
import com.rasalexman.sresult.common.extensions.loggE
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.data.dto.SResult
import com.rasalexman.sresultpresentation.R
import com.rasalexman.sresultpresentation.extensions.*
import com.rasalexman.sresultpresentation.fragments.IBaseFragment
import com.rasalexman.sresultpresentation.viewModels.BaseContextViewModel
import com.rasalexman.sresultpresentation.viewModels.CustomViewModelLazy
import java.lang.ref.WeakReference

abstract class BaseLayout<VM : BaseContextViewModel> : FrameLayout,
    IBaseFragment<VM>, LifecycleOwner {

    /**
     * Fragment ViewModel instance
     */
    override val viewModel: VM? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

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
    )

    inline fun <reified VM : BaseContextViewModel> viewModels(
        noinline fragmentProducer: () -> Fragment = { parentFragment }
    ): Lazy<VM> {
        return CustomViewModelLazy(VM::class, fragmentProducer)
    }

    protected open var parentWeakLifecycle: WeakReference<Lifecycle>? = null
    protected open var parentWeakFragment: WeakReference<Fragment>? = null

    val parentFragment: Fragment
        get() = parentWeakFragment?.get().or {
            findParentFragment()
        }.also {
            parentWeakFragment = WeakReference(it)
        }

    private val parentLifecycle: Lifecycle
        get() {
            return parentWeakLifecycle?.get().or {
                try {
                    parentFragment.viewLifecycleOwner.lifecycle
                } catch (e: Exception) {
                    findParentLifecycle()
                }.also {
                    parentWeakLifecycle = WeakReference(it)
                }
            }
        }

    override var weakContentRef: WeakReference<View>? = null
    override var weakLoadingRef: WeakReference<View>? = null
    override var weakToolbarRef: WeakReference<Toolbar>? = null

    /**
     * Need to attach to parent when inflate data binding view
     */
    open val attachToParent: Boolean = true

    /**
     * View reference getter
     */
    override val contentView: View?
        get() = this

    protected open fun createLayout(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        if (!isInEditMode) {
            val view = LayoutInflater.from(context).inflate(layoutId, this, attachToParent)
            applyAdditionalParameters(context, attrs, defStyleAttr, defStyleRes)
            initLayout(view)
        } else {
            inflate(context, layoutId, this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            addOnCreateViewModelObservers(viewModel)
            addOnViewCreatedViewModelObservers(viewModel)
        }
    }

    override fun onDetachedFromWindow() {
        if (!isInEditMode) {
            val lifecycleOwner = this
            this.clear(lifecycleOwner)
            this.clearOnViewDestroy(lifecycleOwner)
            if (needToClearView) {
                clearView()
            }
            parentWeakLifecycle?.clear()
            parentWeakLifecycle = null
            parentWeakFragment?.clear()
            parentWeakFragment = null
        }
        super.onDetachedFromWindow()
    }

    protected open fun applyAdditionalParameters(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) = Unit

    protected open fun initLayout(view: View) = Unit

    override fun showToast(message: Any?, interval: Int) {
        hideLoading()
        context?.toast(message, interval)
    }

    override fun showLoading() {
        hideKeyboard()
        contentViewLayout?.hide()
        loadingViewLayout?.show()
    }

    override fun hideLoading() {
        hideKeyboard()
        loadingViewLayout?.hide()
        contentViewLayout?.show()
    }

    /**
     * get view [Lifecycle] from its [Context]
     */
    override fun getLifecycle(): Lifecycle {
        return parentLifecycle
    }

    override fun showSuccess(result: SResult.Success<*>) = Unit
    override fun showAlert(alert: SResult.AbstractFailure.Alert) = Unit

    /**
     * Base Result handler function
     */
    override fun onResultHandler(result: SResult<*>) {
        onBaseResultHandler(result)
    }

    /**
     * Navigate by direction [NavDirections]
     */
    override fun navigateTo(direction: Any) {
        (direction as? NavDirections)?.let {
            this.navigateTo(context, findNavController(), it)
        }
    }

    /**
     * Navigate by navResId [Int]
     */
    override fun navigateBy(navResId: Int) {
        this.navigateBy(context, findNavController(), navResId)
    }

    /**
     * Navigate back by pop with navResId
     */
    override fun navigatePopTo(navResId: Int?, isInclusive: Boolean, backArgs: Bundle?) {
        this.navigatePopTo(context, findNavController(), navResId, isInclusive, backArgs)
    }

    /**
     * Navigate to pop on context navigator
     */
    override fun navigatePop(backArgs: Bundle?) {
        this.navigatePop(context, backArgs)
    }

    /**
     * When navigation is broke
     */
    override fun showNavigationError(e: Exception?, navResId: Int?) {
        loggE(
            e,
            "There is no navigation direction from ${this::class.java.simpleName} with contentViewLayout id = $navResId"
        )
    }

    override fun showProgress(progress: Int, message: Any?) = Unit

    ///------- UNUSED SECTION ----///
    override val toolbarTitle: String = ""
    override val toolbarTitleResId: Int? = null
    override val toolbarMenuId: Int? = null
    override val toolbarSubTitle: String = ""

    override fun onMenuItemClick(item: MenuItem?): Boolean = true
    override fun onBackPressed(): Boolean = false
    override fun startActivityForResult(intent: Intent?, requestCode: Int) = Unit
    override fun inflateToolBarMenu(toolbar: Toolbar, menuResId: Int) = Unit
    override fun onToolbarBackPressed() = Unit
    override fun showEmptyLayout() = Unit
    override fun onNextPressed() = Unit
    override fun onAnyDataHandler(data: Any?) = Unit
    override fun toolbarTitleHandler(title: String) = Unit
    override fun toolbarSubTitleHandler(subtitle: String) = Unit
    override fun toolbarMenuHandler(menuResId: Int) = Unit

    override fun showFailure(error: SResult.AbstractFailure.Failure) {
        this.toast(error.getMessage(), error.interval)
    }

    private fun findParentFragment(): Fragment {
        return try {
            this.findFragment()
        } catch (e: Exception) {
            loggE(e, "findParentFragment exception")
            this.context.findPrimaryFragment()!!
        }
    }

    private fun findParentLifecycle(): Lifecycle {
        return try {
            this.context.findPrimaryFragment()!!.lifecycle
        } catch (e: Exception) {
            loggE(e, "findParentLifecycle exception")
            this.context.getOwner<LifecycleOwner>()!!.lifecycle
        }
    }
}