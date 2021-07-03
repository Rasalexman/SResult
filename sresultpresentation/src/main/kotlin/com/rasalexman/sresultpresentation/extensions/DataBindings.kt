package com.rasalexman.sresultpresentation.extensions

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.rasalexman.sresult.common.extensions.or
import com.rasalexman.sresult.models.IDropDownItem
import java.util.*

val selectedPosition = WeakHashMap<String, IDropDownItem>()

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
    if (items != null && lastItems != items) {
        val itemResId = itemLayoutId.or { DEFAULT_DROP_DOWN_LAYOUT_ID }
        val itemsTitle = items.map { it.title }
        view.tag = itemsTitle
        val adapter = ArrayAdapter(
            view.context,
            itemResId,
            itemsTitle
        )
        view.setAdapter(adapter)
    }

    val itemText = selectedItem?.title.orEmpty()
    val lastText = view.text.toString()
    if (lastText != itemText) {
        view.setText(itemText, false)
    }

    view.setOnItemClickListener { _, _, p1, _ ->
        items?.getOrNull(p1)?.let {
            selectedPosition[view.hashCode().toString()] = it
            positionAttrChanged?.onChange()
        }
    }
}

@InverseBindingAdapter(attribute = "selectedItem", event = "positionAttrChanged")
fun getSelectedPosition(view: AutoCompleteTextView): IDropDownItem {
    return selectedPosition[view.hashCode().toString()].or { EMPTY_DROP_DOWN }
}

private const val DEFAULT_DROP_DOWN_LAYOUT_ID = android.R.layout.simple_dropdown_item_1line
private val EMPTY_DROP_DOWN = object : IDropDownItem {
    override val itemId: String = ""
    override val title: String = ""
}
