package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemFavCardBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel

class FavCardViewHolder(
    private val binding: ItemFavCardBinding,
    private val glide: RequestManager,
    private val onCardClicked: (FilmModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var film: FilmModel? = null

    init {
        with (binding) {
            root.setOnClickListener {
                film?.let(onCardClicked)
            }
        }
    }

    fun bindItem(film: FilmModel) {
        this.film = film
        with (binding) {
            tvTitle.text = film.title
            tvTitle.isSelected = true
            tvYear.text = film.year.toString()
            tvRating.text = String.format("%.1f", film.rating)
            glide
                .load(film.imageUrl)
                .placeholder(R.drawable.baseline_downloading_24)
                .error(R.drawable.baseline_error_outline_24)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        val palette = Palette.from(resource.toBitmap()).maximumColorCount(24).generate()
                        val color = palette.getVibrantColor(Color.GRAY)
                        if (Color.luminance(color) <= 0.5) {
                            setColorForInfo(Color.WHITE)
                        } else {
                            setColorForInfo(Color.BLACK)
                        }
                        val grad = GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            intArrayOf(color, color, Color.TRANSPARENT),
                        )
                        grad.cornerRadius = 0f
                        bottomGradient.background = grad
                        return false
                    }
                })
                .into(ivPoster)
        }
    }

    fun setColorForInfo(@ColorInt color: Int) {
        with (binding) {
            tvTitle.setTextColor(color)
            tvYear.setTextColor(color)
            tvRating.setTextColor(color)
            ivStar.imageTintList = ColorStateList.valueOf(color)
        }
    }
}
