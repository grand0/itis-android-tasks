package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
        init()
    }

    private fun init() {
        val gif = requireArguments().getSerializable(ParamsKey.GIF_KEY) as? GifCardModel
        gif?.let { gif ->
            with (binding) {
                Glide
                    .with(this@DetailFragment)
                    .load(gif.url)
                    .placeholder(R.drawable.ic_error)
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

        fun newInstance(gif: GifCardModel): DetailFragment = DetailFragment().apply {
            arguments = bundleOf(ParamsKey.GIF_KEY to gif)
        }
    }
}
