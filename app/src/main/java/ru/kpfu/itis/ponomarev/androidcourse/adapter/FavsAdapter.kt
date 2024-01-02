package ru.kpfu.itis.ponomarev.androidcourse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemFavCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.FavCardViewHolder

class FavsAdapter(
    diffCallback: DiffUtil.ItemCallback<FilmModel>,
    private val glide: RequestManager,
    private val context: Context,
    private val onCardClicked: (FilmModel) -> Unit,
) : ListAdapter<FilmModel, FavCardViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FavCardViewHolder(
            binding = ItemFavCardBinding.inflate(LayoutInflater.from(context), parent, false),
            glide = glide,
            onCardClicked = onCardClicked,
        )

    override fun onBindViewHolder(holder: FavCardViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}
