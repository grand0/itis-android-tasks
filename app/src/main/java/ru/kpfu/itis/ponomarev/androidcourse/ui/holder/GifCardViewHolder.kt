package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel
import ru.kpfu.itis.ponomarev.androidcourse.util.ChipGenerator
import java.lang.Integer.min

class GifCardViewHolder(
    private val binding: ItemGifCardBinding,
    private val glide: RequestManager,
    private val onCardClicked: (GifCardModel, View) -> Unit,
    private val onLikeClicked: (Int, GifModel) -> Unit,
    private val removeOnLongClick: Boolean,
    private val onRemoveRequested: (Int, GifCardModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var item: GifCardModel? = null

    init {
        with (binding) {
            root.setOnClickListener {
                item?.let {
                    onCardClicked(it, ivGif)
                }
            }
            if (removeOnLongClick) {
                root.setOnLongClickListener {
                    overlayDelete.visibility = View.VISIBLE
                    ivDelete.startAnimation(AnimationUtils.loadAnimation(binding.root.context, R.anim.grow))
                    binding.root.startAnimation(AnimationUtils.loadAnimation(binding.root.context, R.anim.shake))
                    true
                }
                overlayDelete.setOnClickListener {
                    item?.let {
                        ivDelete.clearAnimation()
                        binding.root.clearAnimation()
                        overlayDelete.visibility = View.GONE
                        onRemoveRequested(adapterPosition, item!!)
                    }
                }
                overlayDelete.setOnLongClickListener {
                    ivDelete.clearAnimation()
                    binding.root.clearAnimation()
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
            ivGif.transitionName = "item_image${item.id}"

            glide
                .load(item.url)
                .placeholder(R.drawable.ic_loading_square)
                .error(R.drawable.ic_error)
                .into(ivGif)
            tvDescription.text = item.description
            tvDescription.isSelected = true // needed for marquee to work
            cgTags.removeAllViews()
            for (tag in item.tags.subList(0, min(3, item.tags.size))) {
                val chip = ChipGenerator.generate(
                    context = context,
                    text = tag,
                    ensureMinTouchTargetSize = false
                )
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
