package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentAddFilmBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel
import ru.kpfu.itis.ponomarev.androidcourse.service.FilmService
import ru.kpfu.itis.ponomarev.androidcourse.util.clearErrorOnType

class AddFilmFragment : Fragment() {

    private var _binding: FragmentAddFilmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFilmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        with (binding) {
            tilTitle.clearErrorOnType()
            tilYear.clearErrorOnType()
            tilDescription.clearErrorOnType()
            tilImageUrl.clearErrorOnType()

            fabAdd.setOnClickListener {
                val title = etTitle.text
                val year = etYear.text
                val description = etDescription.text
                val imageUrl = etImageUrl.text
                var yearNum = 0
                var valid = true
                if (title.isNullOrEmpty()) {
                    tilTitle.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (year.isNullOrEmpty()) {
                    tilYear.error = getString(R.string.required_error_text)
                    valid = false
                } else {
                    val nullableYear = year.toString().toIntOrNull()
                    if (nullableYear == null || nullableYear < 1800 || nullableYear > 2200) {
                        tilYear.error = "Invalid year"
                        valid = false
                    } else {
                        yearNum = nullableYear
                    }
                }
                if (description.isNullOrEmpty()) {
                    tilDescription.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (imageUrl.isNullOrEmpty()) {
                    tilImageUrl.error = getString(R.string.required_error_text)
                    valid = false
                } else if (!URLUtil.isValidUrl(imageUrl.toString())) {
                    tilImageUrl.error = getString(R.string.invalid_url_error_text)
                    valid = false
                }
                if (valid) {
                    setEnabledForAll(false)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val film = FilmModel(
                            imageUrl = imageUrl.toString(),
                            title = title.toString(),
                            description = description.toString(),
                            year = yearNum,
                        )
                        if (FilmService.checkFilmExists(film)) {
                            requireActivity().runOnUiThread {
                                setEnabledForAll(true)
                                Snackbar.make(
                                    binding.root,
                                    "Such film already exists",
                                    Snackbar.LENGTH_SHORT,
                                ).show()
                            }
                        } else {
                            FilmService.addFilm(film)
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setEnabledForAll(enabled: Boolean) {
        with (binding) {
            tilTitle.isEnabled = enabled
            tilYear.isEnabled = enabled
            tilDescription.isEnabled = enabled
            tilImageUrl.isEnabled = enabled
            fabAdd.isEnabled = enabled
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
