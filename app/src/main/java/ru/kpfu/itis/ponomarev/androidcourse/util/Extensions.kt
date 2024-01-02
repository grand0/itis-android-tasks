package ru.kpfu.itis.ponomarev.androidcourse.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

fun TextInputLayout.clearErrorOnType() {
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            error = null
        }

        override fun afterTextChanged(s: Editable?) { }
    })
}

@SuppressLint("SetTextI18n")
fun TextInputEditText.setupPhoneInput() {
    setOnClickListener {
        if (text.isNullOrEmpty()) {
            setText("79")
        }
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus && text.isNullOrEmpty()) {
            setText("79")
        }
    }
    addTextChangedListener(object : TextWatcher {
        var needToRemoveDigit = false

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            needToRemoveDigit = (after == 0 && s?.substring(start, start + count)
                ?.matches(Regex("\\D")) ?: false)
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) { }

        override fun afterTextChanged(s: Editable?) {
            if (Validators.phoneNeedsFormatting(s.toString())) {
                if (needToRemoveDigit && s?.isNotEmpty() == true) {
                    s.delete(s.length - 1, s.length)
                }
                s?.replace(0, s.length, Validators.formatPhone(s.toString()))
            }
        }
    })
}
