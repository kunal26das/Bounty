package kudos26.bounty.adapter

import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

/**
 * Created by kunal on 10-02-2020.
 */

@BindingAdapter("text")
fun setFieldInputValue(textInputEditText: TextInputEditText, value: Int?) {
    if (value != null) {
        textInputEditText.setText(value.toString())
    } else {
        textInputEditText.setText("")
    }
}

@InverseBindingAdapter(attribute = "text")
fun getFieldInputValue(textInputEditText: TextInputEditText): Int? {
    return try {
        textInputEditText.text.toString().toInt()
    } catch (numberFormatException: NumberFormatException) {
        null
    }
}

@BindingAdapter(value = ["textAttrChanged"])
fun setFieldInputOnChangeListener(
        textInputEditText: TextInputEditText,
        inverseBindingListener: InverseBindingListener
) {
    textInputEditText.addTextChangedListener(
            { _, _, _, _ -> },
            { _, _, _, _ -> },
            { inverseBindingListener.onChange() }
    )
}