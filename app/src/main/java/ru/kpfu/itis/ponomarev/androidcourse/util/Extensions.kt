package ru.kpfu.itis.ponomarev.androidcourse.util

import android.util.DisplayMetrics

fun Int.dp(dm: DisplayMetrics): Int {
    return (this * dm.density).toInt()
}
