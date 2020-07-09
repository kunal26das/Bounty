package kudos26.bounty.view

import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

/**
 * Created by kunal on 10-02-2020.
 */

@BindingAdapter("text")
fun setFieldInputValue(textInputEditText: TextInputEditText, value: Long) {
    textInputEditText.setText(when (value) {
        0L -> ""
        else -> "$value"
    })
}

@InverseBindingAdapter(attribute = "text")
fun getFieldInputValue(textInputEditText: TextInputEditText): Long {
    return try {
        textInputEditText.text.toString().toLong()
    } catch (e: NumberFormatException) {
        0
    }
}

@BindingAdapter(value = ["textAttrChanged"])
fun setFieldInputOnChangeListener(
        textInputEditText: TextInputEditText,
        inverseBindingListener: InverseBindingListener
) {
    textInputEditText.doAfterTextChanged {
        inverseBindingListener.onChange()
    }
}