package ru.kpfu.itis.ponomarev.androidcourse.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel

class FilmDiffUtilItemCallback : DiffUtil.ItemCallback<FilmModel>() {
    override fun areItemsTheSame(oldItem: FilmModel, newItem: FilmModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FilmModel, newItem: FilmModel): Boolean {
        return oldItem == newItem
    }
}
