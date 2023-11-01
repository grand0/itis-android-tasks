package ru.kpfu.itis.ponomarev.androidcourse.util

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.chip.Chip

object ChipGenerator {
    fun generate(context: Context, text: String, ensureMinTouchTargetSize: Boolean = true)
        = Chip(context).apply {
            this.text = text
            setEnsureMinTouchTargetSize(ensureMinTouchTargetSize)
            chipStrokeWidth = 1.dp(context.theme.resources.displayMetrics).toFloat()
            val colorStateList = ColorStateList.valueOf(ColorGenerator.generateForString(text))
            setTextColor(colorStateList)
            chipStrokeColor = colorStateList
            chipBackgroundColor = colorStateList.withAlpha(30)
        }
}
