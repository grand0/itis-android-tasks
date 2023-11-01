package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.adapter.FeedAdapter
import ru.kpfu.itis.ponomarev.androidcourse.adapter.diffutil.GifDiffUtilItemCallback
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentFeedBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.HorizontalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.VerticalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.GifCardViewHolder
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
        val gifsCount = requireArguments().getInt(ParamsKey.GIFS_COUNT_KEY)

        feedAdapter = FeedAdapter(
            diffCallback = GifDiffUtilItemCallback(),
            glide = Glide.with(this),
            context = requireContext(),
            onAddClicked = ::onAddClicked,
            onCardClicked = ::onCardClicked,
            onLikeClicked = ::onLikeClicked,
            removeOnLongClick = gifsCount > 12,
            onRemoveRequested = ::onRemoveRequested,
        )

        if (GifRepository.getFeedList().isEmpty()) {
            GifRepository.initFeedList(gifsCount)
        }
        feedAdapter?.setItems(GifRepository.getFeedList())

        with (binding) {
            if (gifsCount <= 12) {
                rvFeed.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
                rvFeed.addItemDecoration(HorizontalMarginDecorator(offset = 16.dp(resources.displayMetrics)))
                rvFeed.addItemDecoration(VerticalMarginDecorator(offset = 8.dp(resources.displayMetrics)))

                ItemTouchHelper(object : ItemTouchHelper.Callback() {
                    override fun getMovementFlags(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ): Int = when (viewHolder) {
                        is GifCardViewHolder -> makeMovementFlags(0, ItemTouchHelper.LEFT)
                        else -> 0
                    }

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        (GifRepository.getFeedList()[position] as? GifCardModel)?.let { gif ->
                            onRemoveRequested(position, gif)
                        }
                    }
                }).attachToRecyclerView(rvFeed)
            } else {
                rvFeed.layoutManager = StaggeredGridLayoutManager(
                    2,
                    LinearLayoutManager.VERTICAL,
                )
                rvFeed.addItemDecoration(HorizontalMarginDecorator(offset = 8.dp(resources.displayMetrics)))
                rvFeed.addItemDecoration(VerticalMarginDecorator(offset = 8.dp(resources.displayMetrics)))
            }

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

    private fun removeGif(position: Int) {
        GifRepository.removeItem(position)
        feedAdapter?.removeItem(position)
    }

    private fun insertGif(position: Int, gif: GifCardModel) {
        GifRepository.addItem(position, gif)
        feedAdapter?.insertItem(position, gif)
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

    private fun onRemoveRequested(position: Int, gif: GifCardModel) {
        removeGif(position)
        showUndoRemoveSnackbar(position, gif)
    }

    private fun showUndoRemoveSnackbar(position: Int, gif: GifCardModel) {
        this.view?.let {
            Snackbar
                .make(
                    it,
                    getString(R.string.deleted_gif_info, gif.description),
                    Snackbar.LENGTH_LONG
                )
                .setAction(R.string.undo_btn_text) {
                    insertGif(position, gif)
                }
                .show()
        }
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
