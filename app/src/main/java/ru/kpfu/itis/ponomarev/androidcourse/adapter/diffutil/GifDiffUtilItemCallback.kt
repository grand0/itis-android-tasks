package ru.kpfu.itis.ponomarev.androidcourse.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import ru.kpfu.itis.ponomarev.androidcourse.model.GifButtonModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifDateModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel

class GifDiffUtilItemCallback : DiffUtil.ItemCallback<GifModel>() {

    override fun areItemsTheSame(oldItem: GifModel, newItem: GifModel): Boolean {
        return when (oldItem) {
            is GifCardModel -> oldItem.id == (newItem as? GifCardModel)?.id
            is GifDateModel -> oldItem.date == (newItem as? GifDateModel)?.date
            is GifButtonModel -> true
        }
    }

    override fun areContentsTheSame(oldItem: GifModel, newItem: GifModel): Boolean {
        return oldItem == newItem
    }
}
