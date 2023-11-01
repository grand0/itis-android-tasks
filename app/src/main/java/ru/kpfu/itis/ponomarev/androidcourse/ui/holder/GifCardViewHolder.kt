package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel

class GifCardViewHolder(
    private val binding: ItemGifCardBinding,
    private val glide: RequestManager,
    private val onCardClicked: (GifCardModel) -> Unit,
    private val onLikeClicked: (Int, GifModel) -> Unit,
    private val removeOnLongClick: Boolean,
    private val onRemoveRequested: (Int, GifCardModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var item: GifCardModel? = null

    init {
        with (binding) {
            root.setOnClickListener {
                item?.let(onCardClicked)
            }
            if (removeOnLongClick) {
                root.setOnLongClickListener {
                    overlayDelete.visibility = View.VISIBLE
                    true
                }
                overlayDelete.setOnClickListener {
                    item?.let {
                        overlayDelete.visibility = View.GONE
                        onRemoveRequested(adapterPosition, item!!)
                    }
                }
                overlayDelete.setOnLongClickListener {
                    overlayDelete.visibility = View.GONE
                    true
                }
            }
            ivLikeBtn.setOnClickListener {
                item?.let {
                    val data = it.copy(isLiked = !it.isLiked)
                    changeLikeStatus(data.isLiked)
                    onLikeClicked(adapterPosition, data)
                }
            }
        }
    }

    fun bindItem(item: GifCardModel, context: Context) {
        this.item = item
        with (binding) {
            glide
                .load(item.url)
                .placeholder(R.drawable.ic_error)
                .into(ivGif)
            tvDescription.text = item.description
            cgTags.removeAllViews()
            for (tag in item.tags) {
                val chip = Chip(context).apply {
                    text = tag
                    setEnsureMinTouchTargetSize(false)
                }
                cgTags.addView(chip)
            }
            changeLikeStatus(item.isLiked)
        }
    }

    private fun changeLikeStatus(isLiked: Boolean) {
        val likeDrawable = if (isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off
        binding.ivLikeBtn.setImageResource(likeDrawable)
    }
}
