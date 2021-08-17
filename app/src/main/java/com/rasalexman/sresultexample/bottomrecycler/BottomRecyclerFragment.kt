package com.rasalexman.sresultexample.bottomrecycler

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.rasalexman.easyrecyclerbinding.DiffCallback
import com.rasalexman.easyrecyclerbinding.createRecyclerConfig
import com.rasalexman.sresult.common.extensions.navigatePopTo
import com.rasalexman.sresultexample.BR
import com.rasalexman.sresultexample.R
import com.rasalexman.sresultexample.databinding.FragmentBottomRecyclerBinding
import com.rasalexman.sresultexample.databinding.ItemUserBinding
import com.rasalexman.sresultexample.users.UserItem
import com.rasalexman.sresultpresentation.databinding.BaseBindingBottomDialogFragment
import com.rasalexman.sresultpresentation.extensions.string
import com.rasalexman.sresultpresentation.extensions.windowHeight


class BottomRecyclerFragment :
    BaseBindingBottomDialogFragment<FragmentBottomRecyclerBinding, BottomRecyclerViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_bottom_recycler

    override fun getTheme(): Int {
        return R.style.ModalBottomSheetDialog
    }

    override val toolbarMenuId: Int
        get() = R.menu.menu_refresh

    override val toolbarTitle: String
        get() = string(R.string.title_bottom_recycler)

    override val viewModel: BottomRecyclerViewModel by viewModels()

    override fun initBinding(binding: FragmentBottomRecyclerBinding) {
        binding.rvConfig = createRecyclerConfig<UserItem, ItemUserBinding> {
            itemId = BR.item
            layoutId = R.layout.item_user
            onItemClick = { item, _ ->
                onResultHandler(navigatePopTo(backArgs = bundleOf(KEY_SELECTED to item.fullName)))
            }

            diffUtilCallback = object : DiffCallback<UserItem>() {
                override fun areItemsTheSame(
                    oldItem: UserItem,
                    newItem: UserItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.layoutParams.height = this.windowHeight
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionRefresh) {
            //dismiss()
            viewModel.onShowEmpty()
        }
        return super.onMenuItemClick(item)
    }

    companion object {
        const val KEY_SELECTED = "KEY_SELECTED"
    }
}