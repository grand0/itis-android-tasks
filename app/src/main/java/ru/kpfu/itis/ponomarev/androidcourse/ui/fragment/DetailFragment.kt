package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentDetailBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.util.ParamsKey

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        init()
    }

    private fun init() {
        val transitionName = requireArguments().getString(ParamsKey.TRANSITION_NAME_KEY)
        val gif = requireArguments().getSerializable(ParamsKey.GIF_KEY) as? GifCardModel
        gif?.let { gif ->
            with (binding) {
                ivGif.transitionName = transitionName

                Glide
                    .with(this@DetailFragment)
                    .load(gif.url)
                    .placeholder(R.drawable.ic_error)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }
                    })
                    .into(ivGif)
                tvDescription.text = gif.description
                for (tag in gif.tags) {
                    val chip = Chip(context)
                    chip.text = tag
                    cgTags.addView(chip)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG"

        fun newInstance(gif: GifCardModel, transitionName: String): DetailFragment = DetailFragment().apply {
            arguments = bundleOf(
                ParamsKey.GIF_KEY to gif,
                ParamsKey.TRANSITION_NAME_KEY to transitionName
            )
        }
    }
}
