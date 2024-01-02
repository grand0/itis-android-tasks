package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.adapter.FavsAdapter
import ru.kpfu.itis.ponomarev.androidcourse.adapter.FeedAdapter
import ru.kpfu.itis.ponomarev.androidcourse.adapter.diffutil.FilmDiffUtilItemCallback
import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentHomeBinding
import ru.kpfu.itis.ponomarev.androidcourse.event.FilmAddedEventManager
import ru.kpfu.itis.ponomarev.androidcourse.event.FilmChangedEventManager
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.service.FilmService
import ru.kpfu.itis.ponomarev.androidcourse.session.AppSession
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.HorizontalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.ui.decoration.VerticalMarginDecorator
import ru.kpfu.itis.ponomarev.androidcourse.ui.layoutmanager.NpaStaggeredGridLayoutManager
import ru.kpfu.itis.ponomarev.androidcourse.util.toPx

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val films = mutableListOf<FilmModel>()
    private val favorites = mutableListOf<FilmModel>()
    private var filmsAdapter: FeedAdapter? = null
    private var favoritesAdapter: FavsAdapter? = null

    private var filmAddedListenerCancellationToken: FilmAddedEventManager.CancellationToken? = null
    private var filmChangedListenerCancellationToken: FilmChangedEventManager.CancellationToken? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        if (AppSession.authorizedUser == null) {
            val email = AppConfig.userEmail
            val passwordHash = AppConfig.userPasswordHash
            if (email != null && passwordHash != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    AppSession.authorizeUser(email, passwordHash)
                    val user = AppSession.authorizedUser
                    if (user == null) { // email and password are wrong
                        AppConfig.clearUserConfig()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, LoginFragment())
                            .commit()
                    } else {
                        if (user.deletionRequestTimestamp == null) {
                            requireActivity().runOnUiThread {
                                initViews()
                            }
                        } else {
                            parentFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    AccountDeletedFragment()
                                )
                                .commit()
                        }
                    }
                }
            }
        } else {
            initViews()
        }
    }

    private fun initViews() {
        val glide = Glide.with(this)
        filmsAdapter = FeedAdapter(
            diffCallback = FilmDiffUtilItemCallback(),
            glide = glide,
            context = requireContext(),
            onCardClicked = ::onCardClicked,
            onFavClicked = ::onFavClicked,
        )
        favoritesAdapter = FavsAdapter(
            diffCallback = FilmDiffUtilItemCallback(),
            glide = glide,
            context = requireContext(),
            onCardClicked = ::onCardClicked,
        )
        val filmsLoadingJob = lifecycleScope.launch(Dispatchers.IO) {
            films.addAll(FilmService.getAllFilms())
            favorites.addAll(films.filter(FilmModel::isFavorite))
            filmsAdapter?.submitList(films)
            favoritesAdapter?.submitList(favorites)
            delay(1000)
        }

        with (binding) {
            toolbar.setOnMenuItemClickListener {
                if (it.title == getString(R.string.profile_menu_item)) {
                    parentFragmentManager.beginTransaction()
                        .add(
                            R.id.fragment_container,
                            ProfileFragment()
                        )
                        .addToBackStack(null)
                        .commit()
                    return@setOnMenuItemClickListener true
                }
                false
            }
            val filmsLayoutManager = NpaStaggeredGridLayoutManager(
                2,
                LinearLayoutManager.VERTICAL,
            )
            rvFeed.layoutManager = filmsLayoutManager
            rvFeed.addItemDecoration(HorizontalMarginDecorator(offset = requireContext().toPx(4)))
            rvFeed.addItemDecoration(VerticalMarginDecorator(offset = requireContext().toPx(4)))
            rvFeed.adapter = filmsAdapter
            rvFeed.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) fabAdd.hide()
                    else if (dy < 0) fabAdd.show()
                }
            })
            ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        onFilmRemoved(position)
                    }
                }
            ).attachToRecyclerView(rvFeed)

            rvFavorites.addItemDecoration(HorizontalMarginDecorator(offset = requireContext().toPx(4)))
            rvFavorites.adapter = favoritesAdapter

            rvFeed.loadSkeleton(R.layout.item_film_card) {
                itemCount(6)
            }
            rvFavorites.loadSkeleton(R.layout.item_film_card) {
                itemCount(3)
            }
            filmsLoadingJob.invokeOnCompletion {
                requireActivity().runOnUiThread {
                    rvFeed.hideSkeleton()
                    rvFavorites.hideSkeleton()
                    if (filmsAdapter?.itemCount == 0) {
                        rvFeed.isVisible = false
                        layoutEmptyFilmsList.isVisible = true
                    }
                    if (favoritesAdapter?.itemCount == 0) {
                        rvFavorites.isVisible = false
                        layoutEmptyFavList.isVisible = true
                    }
                }
            }

            fabAdd.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        AddFilmFragment(),
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }

        filmAddedListenerCancellationToken = FilmAddedEventManager.addListener(::onFilmAdded)
        filmChangedListenerCancellationToken = FilmChangedEventManager.addListener(::onFilmChanged)
    }

    private fun onFilmRemoved(position: Int) {
        val film = films.removeAt(position)
        val favIndex = favorites.indexOfFirst { it.id == film.id }
        if (favIndex != -1) {
            favorites.remove(film)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            FilmService.deleteFilm(film)
            requireActivity().runOnUiThread {
                filmsAdapter?.notifyItemRemoved(position)
                if (favIndex != -1) {
                    favoritesAdapter?.notifyItemRemoved(favIndex)
                }
                if (films.size == 0) {
                    binding.layoutEmptyFilmsList.isVisible = true
                    binding.rvFeed.isVisible = false
                }
                if (favorites.size == 0) {
                    binding.layoutEmptyFavList.isVisible = true
                    binding.rvFavorites.isVisible = false
                }
            }
        }
    }

    private fun onFilmAdded(film: FilmModel) {
        var index = films.binarySearch { film.year.compareTo(it.year) }
        if (index < 0) index = -index - 1
        films.add(index, film)
        requireActivity().runOnUiThread {
            filmsAdapter?.notifyItemInserted(index)
            if (films.size == 1) {
                binding.layoutEmptyFilmsList.isVisible = false
                binding.rvFeed.isVisible = true
            }
        }
    }

    private fun onCardClicked(film: FilmModel) {
        parentFragmentManager.beginTransaction()
            .add(
                R.id.fragment_container,
                FilmFragment.newInstance(film),
                FilmFragment.FILM_FRAGMENT_TAG,
            )
            .addToBackStack(null)
            .commit()
    }

    private fun onFavClicked(film: FilmModel, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val favIndex: Int?
            if (FilmService.isFavorite(film.id)) {
                FilmService.unmarkFavorite(film.id)
                films[position].isFavorite = false
                favIndex = favorites.indexOf(film)
                favorites.removeAt(favIndex)
            } else {
                FilmService.markFavorite(film.id)
                films[position].isFavorite = true
                favIndex = null
                favorites.add(0, film)
            }
            requireActivity().runOnUiThread {
                filmsAdapter?.notifyItemChanged(position)
                if (favIndex == null) {
                    favoritesAdapter?.notifyItemInserted(0)
                    binding.rvFavorites.scrollToPosition(0)
                } else if (favIndex != -1) {
                    favoritesAdapter?.notifyItemRemoved(favIndex)
                }
                if (favorites.size == 0) {
                    binding.rvFavorites.isVisible = false
                    binding.layoutEmptyFavList.isVisible = true
                } else if (favIndex == null && favorites.size == 1) {
                    binding.rvFavorites.isVisible = true
                    binding.layoutEmptyFavList.isVisible = false
                }
            }
        }
    }

    private fun onFilmChanged(film: FilmModel) {
        var index = films.indexOfFirst { it.id == film.id }
        if (index != -1) {
            films[index] = film
            filmsAdapter?.notifyItemChanged(index)
        }
        if (film.isFavorite) {
            index = favorites.indexOfFirst { it.id == film.id }
            if (index != -1) {
                favorites[index] = film
                favoritesAdapter?.notifyItemChanged(index)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        filmAddedListenerCancellationToken?.cancel()
        filmChangedListenerCancellationToken?.cancel()
        super.onDestroyView()
    }
}
