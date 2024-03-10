package ru.kpfu.itis.ponomarev.androidcourse.presentation.view.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.BuildConfig
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.presentation.viewmodel.WeatherViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    weatherViewModel.loadingState.collect { isLoading ->
                        if (isLoading) {
                            binding.pbLoading.isGone = false
                        } else {
                            binding.pbLoading.isGone = true
                        }
                    }
                }
                launch {
                    weatherViewModel.currentWeatherState.collect { model ->
                        if (model == null) {
                            with (binding) {
                                tvWeatherCityLabel.isGone = true
                                llWeather.isGone = true
                                ivIcon.isGone = true
                            }
                        } else {
                            with (binding) {
                                tvWeatherCityLabel.isGone = false
                                llWeather.isGone = false
                                ivIcon.isGone = false
                                pbIcon.isGone = false
                            }

                            Glide.with(this@MainFragment)
                                .load(BuildConfig.OPENWEATHER_API_ICON_URL_TEMPLATE.format(model.weather.first().icon))
                                .error(R.drawable.outline_error)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.pbIcon.isGone = true
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable,
                                        model: Any,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.pbIcon.isGone = true
                                        return false
                                    }
                                })
                                .into(binding.ivIcon)
                            binding.tvWeatherCityLabel.text = getString(R.string.weather_in_city_label, model.city, model.sys.country)
                            binding.tvTemp.text = getString(R.string.temp_value_text, model.main.temp.toInt())
                        }
                    }
                }
                launch {
                    for (error in weatherViewModel.errorsChannel) {
                        val msg = error.message ?: getString(R.string.unknown_error)
                        Snackbar.make(
                            binding.root,
                            msg,
                            Snackbar.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (v.text.isNotBlank() && (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH)) {
                val city = v.text.trim().toString()
                weatherViewModel.updateCurrentWeather(city)
            }
            true
        }

        binding.llWeather.isGone = false
        binding.ivIcon.isGone = false
        binding.pbLoading.isGone = false
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
