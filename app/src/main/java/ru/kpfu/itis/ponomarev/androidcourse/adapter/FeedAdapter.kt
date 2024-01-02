package ru.kpfu.itis.ponomarev.androidcourse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemFilmCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.FilmCardViewHolder

class FeedAdapter(
    diffCallback: ItemCallback<FilmModel>,
    private val glide: RequestManager,
    private val context: Context,
    private val onCardClicked: (FilmModel) -> Unit,
    private val onFavClicked: (FilmModel, Int) -> Unit,
) : ListAdapter<FilmModel, FilmCardViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FilmCardViewHolder(
            binding = ItemFilmCardBinding.inflate(LayoutInflater.from(context), parent, false),
            glide = glide,
            onCardClicked = onCardClicked,
            onFavClicked = onFavClicked,
        )

    override fun onBindViewHolder(holder: FilmCardViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}
