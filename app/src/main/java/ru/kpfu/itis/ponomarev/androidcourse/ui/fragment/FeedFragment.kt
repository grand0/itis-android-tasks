package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.adapter.FeedAdapter
import ru.kpfu.itis.ponomarev.androidcourse.adapter.diffutil.GifDiffUtilItemCallback
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentFeedBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.HorizontalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.VerticalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.util.GifRepository
import ru.kpfu.itis.ponomarev.androidcourse.util.ParamsKey
import ru.kpfu.itis.ponomarev.androidcourse.util.dp

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private var feedAdapter: FeedAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        feedAdapter = FeedAdapter(
            diffCallback = GifDiffUtilItemCallback(),
            glide = Glide.with(this),
            context = requireContext(),
            onAddClicked = ::onAddClicked,
            onCardClicked = ::onCardClicked,
            onLikeClicked = ::onLikeClicked,
        )

        val gifsCount = requireArguments().getInt(ParamsKey.GIFS_COUNT_KEY)
        if (GifRepository.getFeedList().isEmpty()) {
            GifRepository.initFeedList(gifsCount)
        }
        feedAdapter?.setItems(GifRepository.getFeedList())

        with (binding) {
            val layoutManager = if (gifsCount <= 12)
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                ).also {
                    rvFeed.addItemDecoration(HorizontalMarginDecorator(offset = 16.dp(resources.displayMetrics)))
                    rvFeed.addItemDecoration(VerticalMarginDecorator(offset = 8.dp(resources.displayMetrics)))
                }
            else
                StaggeredGridLayoutManager(
                    2,
                    LinearLayoutManager.VERTICAL,
                ).apply {
                    rvFeed.addItemDecoration(HorizontalMarginDecorator(offset = 8.dp(resources.displayMetrics)))
                    rvFeed.addItemDecoration(VerticalMarginDecorator(offset = 8.dp(resources.displayMetrics)))
                }

            rvFeed.layoutManager = layoutManager
            rvFeed.adapter = feedAdapter
        }
    }

    private fun onAddClicked() {
        AddGifsBottomSheetDialogFragment(
            onAddClicked = ::addGifs
        ).show(
            childFragmentManager,
            AddGifsBottomSheetDialogFragment.ADD_GIFS_BOTTOM_SHEET_DIALOG_FRAGMENT_TAG
        )
    }

    private fun addGifs(n: Int) {
        GifRepository.addRandomCards(n)
        feedAdapter?.setItems(GifRepository.getFeedList())
    }

    private fun onCardClicked(gif: GifCardModel) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                DetailFragment.newInstance(gif),
                DetailFragment.DETAIL_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    private fun onLikeClicked(position: Int, gif: GifModel) {
        GifRepository.setItem(position, gif)
        feedAdapter?.updateItem(position, gif)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FEED_FRAGMENT_TAG = "FEED_FRAGMENT_TAG"

        fun newInstance(gifsCount: Int) = FeedFragment().apply {
            arguments = bundleOf(ParamsKey.GIFS_COUNT_KEY to gifsCount)
        }
    }
}
