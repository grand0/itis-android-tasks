package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentFilmBinding
import ru.kpfu.itis.ponomarev.androidcourse.event.FilmChangedEventManager
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.service.FilmService
import ru.kpfu.itis.ponomarev.androidcourse.util.Keys

class FilmFragment : Fragment() {

    private var _binding: FragmentFilmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    @Suppress("NAME_SHADOWING")
    private fun init() {
        val film = requireArguments().getSerializable(Keys.FILM_MODEL_KEY) as? FilmModel
        film?.let { film ->
            with (binding) {
                Glide.with(this@FilmFragment)
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
                            val grad = GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                intArrayOf(color, color, Color.TRANSPARENT),
                            )
                            grad.cornerRadius = 0f
                            topGradient.background = grad
                            return false
                        }
                    })
                    .into(ivPoster)
                tvTitle.text = film.title
                tvDescription.text = film.description
                tvYear.text = film.year.toString()
                tvRating.text = String.format("%.1f", film.rating)
                lifecycleScope.launch(Dispatchers.IO) {
                    val userRating = FilmService.getRatingOfUser(film.id)
                    requireActivity().runOnUiThread {
                        ratingBar.rating = userRating.toFloat()
                    }
                }
                ratingBar.setOnRatingBarChangeListener { _, rating, isUser ->
                    if (isUser) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            FilmService.rateFilm(
                                filmId = film.id,
                                rating = rating.toInt(),
                            )
                            film.rating = FilmService.getFilmRating(film)
                            requireActivity().runOnUiThread {
                                tvRating.text = String.format("%.1f", film.rating)
                                FilmChangedEventManager.notifyEvent(film)
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.film_rated_msg, rating.toInt()),
                                    Snackbar.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FILM_FRAGMENT_TAG = "FILM_FRAGMENT_TAG"

        fun newInstance(film: FilmModel) = FilmFragment().apply {
            arguments = bundleOf(Keys.FILM_MODEL_KEY to film)
        }
    }
}