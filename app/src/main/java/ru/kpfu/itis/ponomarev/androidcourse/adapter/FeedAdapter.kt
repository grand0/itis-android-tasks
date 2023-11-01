package ru.kpfu.itis.ponomarev.androidcourse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifButtonBinding
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifDateBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifButtonModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifDateModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.GifButtonViewHolder
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.GifCardViewHolder
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.GifDateViewHolder
import java.lang.RuntimeException

class FeedAdapter(
    diffCallback: DiffUtil.ItemCallback<GifModel>,
    private val glide: RequestManager,
    private val context: Context,
    private val onAddClicked: () -> Unit,
    private val onCardClicked: (GifCardModel) -> Unit,
    private val onLikeClicked: (Int, GifModel) -> Unit,
    private val removeOnLongClick: Boolean,
    private val onRemoveRequested: (Int, GifCardModel) -> Unit,
) : ListAdapter<GifModel, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder = when (viewType) {
        R.layout.item_gif_button -> GifButtonViewHolder(
            binding = ItemGifButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick = onAddClicked
        )
        R.layout.item_gif_date -> GifDateViewHolder(
            binding = ItemGifDateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
        R.layout.item_gif_card -> GifCardViewHolder(
            binding = ItemGifCardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide = glide,
            onCardClicked = onCardClicked,
            onLikeClicked = onLikeClicked,
            removeOnLongClick = removeOnLongClick,
            onRemoveRequested = onRemoveRequested,
        )
        else -> throw RuntimeException("Unknown view holder in FeedAdapter")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GifButtonViewHolder -> {
                // if layout manager is staggered grid set this view holder to use all span area
                val layoutParams = (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                layoutParams?.isFullSpan = true
            }
            is GifDateViewHolder -> {
                // if layout manager is staggered grid set this view holder to use all span area
                val layoutParams = (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                layoutParams?.isFullSpan = true

                holder.bindItem(getItem(position) as GifDateModel)
            }
            is GifCardViewHolder -> holder.bindItem(getItem(position) as GifCardModel, context)
        }
    }

    fun setItems(items: List<GifModel>) {
        // for some reason list doesn't get updated if you call submitList(items), so i am
        // creating a deep copy of list in order to update it.
        //
        // https://stackoverflow.com/a/50031492
        // it seems that for some reason lists are equal even when they're not.
        // i don't think there would be any major performance issues, because diffutil
        // should work as expected, redrawing only views that are changed
        val itemsCopy = items.map {
            when (it) {
                is GifButtonModel -> it
                is GifCardModel -> it.copy()
                is GifDateModel -> it.copy()
            }
        }
        submitList(itemsCopy)
    }

    fun updateItem(position: Int, item: GifModel) {
        val list = currentList.toMutableList()
        list[position] = item
        submitList(list)
    }

    fun removeItem(position: Int) {
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
    }

    fun insertItem(position: Int, item: GifModel) {
        val list = currentList.toMutableList()
        list.add(position, item)
        submitList(list)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GifButtonModel -> R.layout.item_gif_button
            is GifDateModel -> R.layout.item_gif_date
            is GifCardModel -> R.layout.item_gif_card
        }
    }
}
