package com.rasalexman.sresultpresentation.extensions

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("hintLabel")
fun setHintResource(view: TextInputLayout, hintResId: Int?) {
    hintResId?.let {
        view.hint = view.context.getString(hintResId)
    }
}

@BindingAdapter(
    value = ["items", "selectedItem", "selectedValue", "positionAttrChanged", "valueAttrChanged"],
    requireAll = false
)
fun setItemsAdapter(
    view: AutoCompleteTextView,
    items: List<String>?,
    selectedItem: Int?,
    selectedValue: String?,
    changeListener: InverseBindingListener?,
    valueListener: InverseBindingListener?
) {
    val adapter = ArrayAdapter(
        view.context,
        android.R.layout.simple_dropdown_item_1line,
        items.orEmpty()
    )
    view.setAdapter(adapter)
    selectedValue?.let {
        view.setText(selectedValue)
    }
    selectedItem?.let {
        view.listSelection = it
    }
    view.setOnItemClickListener { _, _, _, _ ->
        changeListener?.onChange()
        valueListener?.onChange()
    }
}

@InverseBindingAdapter(attribute = "selectedItem", event = "positionAttrChanged")
fun getSelectedPosition(view: AutoCompleteTextView): Int {
    return view.listSelection
}

@InverseBindingAdapter(attribute = "selectedValue", event = "valueAttrChanged")
fun getSelectedValue(view: AutoCompleteTextView): String {
    return view.text.toString()
}