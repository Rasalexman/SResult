

package com.rasalexman.sresultpresentation.extensions

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.rasalexman.sresult.common.extensions.doOnDefault
import com.rasalexman.sresult.common.extensions.listSize
import com.rasalexman.sresult.models.IDropDownItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

val selectedPosition = WeakHashMap<Int, IDropDownItem>()

@Suppress("UNCHECKED_CAST")
@BindingAdapter(
    value = ["items", "selectedItem", "itemLayoutId", "positionAttrChanged"],
    requireAll = false
)
fun setItemsAdapter(
    view: AutoCompleteTextView,
    items: List<IDropDownItem>?,
    selectedItem: IDropDownItem?,
    itemLayoutId: Int?,
    positionAttrChanged: InverseBindingListener?
) {
    selectedPosition.clear()

    val lastItems = view.tag as? List<String>
    val lastSize = lastItems.listSize()
    val itemsSize = items.listSize()
    if(lastSize != itemsSize) {
        println("-------> itemsSize = $itemsSize")
        val itemResId = itemLayoutId ?: android.R.layout.simple_dropdown_item_1line
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val itemsTitle = doOnDefault { items.orEmpty().map { it.title } }
            view.tag = itemsTitle
            val adapter = ArrayAdapter(
                view.context,
                itemResId,
                itemsTitle
            )
            view.setAdapter(adapter)
        }
    }

    selectedItem?.let {
        val lastText = view.text.toString()
        if(lastText != it.title) {
            view.setText(it.title, false)
        }
    }

    view.setOnItemClickListener { _, _, p1, _ ->
        items?.getOrNull(p1)?.let {
            selectedPosition[view.hashCode()] = it
            positionAttrChanged?.onChange()
        }
    }
}

@InverseBindingAdapter(attribute = "selectedItem", event = "positionAttrChanged")
fun getSelectedPosition(view: AutoCompleteTextView): IDropDownItem {
    val selectedItem = selectedPosition[view.hashCode()]!!
    println("-------> selectedItem = $selectedItem")
    return selectedItem
}
