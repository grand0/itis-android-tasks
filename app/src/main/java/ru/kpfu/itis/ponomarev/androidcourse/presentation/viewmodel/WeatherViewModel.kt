package ru.kpfu.itis.ponomarev.androidcourse.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.data.ExceptionHandlerDelegate
import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel
import ru.kpfu.itis.ponomarev.androidcourse.domain.usecase.GetCurrentWeatherUseCase
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val exceptionHandlerDelegate: ExceptionHandlerDelegate
) : ViewModel() {

    private val _currentWeatherState = MutableStateFlow<WeatherModel?>(null)
    val currentWeatherState get() = _currentWeatherState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState get() = _loadingState.asStateFlow()

    val errorsChannel = Channel<Exception>()

    fun updateCurrentWeather(city: String) {
        _currentWeatherState.value = null
        _loadingState.value = true
        viewModelScope.launch {
            try {
                _currentWeatherState.value = getCurrentWeatherUseCase(city)
            } catch (ex: Exception) {
                exceptionHandlerDelegate.handleException(ex).also {
                    errorsChannel.send(it)
                }
            } finally {
                _loadingState.value = false
            }
        }
    }
}
