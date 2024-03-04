package ru.kpfu.itis.ponomarev.androidcourse.util

import android.content.Context
import android.util.TypedValue

fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()