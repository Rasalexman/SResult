package com.rasalexman.sresultpresentation.extensions

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.rasalexman.sresult.common.extensions.orZero
import java.util.*

val selectedPosition = WeakHashMap<Int, IDropDownItem>()

interface IDropDownItem {
    val id: String
    val title: String
}

@BindingAdapter(
    value = ["items", "selectedItem", "positionAttrChanged"],
    requireAll = false
)
fun setItemsAdapter(
    view: AutoCompleteTextView,
    items: List<IDropDownItem>?,
    selectedItem: IDropDownItem?,
    positionAttrChanged: InverseBindingListener?
) {
    selectedPosition.clear()

    val adapter = ArrayAdapter(
        view.context,
        android.R.layout.simple_dropdown_item_1line,
        items.orEmpty().map { it.title }
    )
    view.setAdapter(adapter)

    selectedItem?.let {
        view.setText(it.title, false)
        view.performCompletion()
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
