package ru.kpfu.itis.ponomarev.androidcourse.ui.layoutmanager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.StaggeredGridLayoutManager

// https://stackoverflow.com/q/30220771
class NpaStaggeredGridLayoutManager : StaggeredGridLayoutManager {

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    override fun supportsPredictiveItemAnimations(): Boolean = false
}