package ru.kpfu.itis.ponomarev.androidcourse.domain.usecase

import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel
import ru.kpfu.itis.ponomarev.androidcourse.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {

    suspend operator fun invoke(city: String): WeatherModel {
        return weatherRepository.getCurrentWeather(city)
    }
}