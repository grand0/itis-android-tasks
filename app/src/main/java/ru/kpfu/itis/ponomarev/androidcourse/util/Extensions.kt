package ru.kpfu.itis.ponomarev.androidcourse.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import ru.kpfu.itis.ponomarev.androidcourse.R

fun Context.toPx(dp: Int) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics,
)

fun Snackbar.setIcon(@DrawableRes resId: Int): Snackbar {
    return setIcon(AppCompatResources.getDrawable(context, resId))
}

fun Snackbar.setIcon(icon: Drawable?): Snackbar {
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
        setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        compoundDrawablePadding = if (icon != null) {
            context.resources.getDimensionPixelOffset(R.dimen.snackbar_icon_padding)
        } else {
            0
        }
    }
    return this
}
