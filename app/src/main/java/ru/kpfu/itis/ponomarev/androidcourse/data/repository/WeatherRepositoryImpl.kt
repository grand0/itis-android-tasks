package ru.kpfu.itis.ponomarev.androidcourse.data.repository

import ru.kpfu.itis.ponomarev.androidcourse.data.exception.EmptyWeatherResponseException
import ru.kpfu.itis.ponomarev.androidcourse.data.remote.OpenWeatherApi
import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel
import ru.kpfu.itis.ponomarev.androidcourse.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String): WeatherModel {
        return api.getCurrentWeather(city) ?: throw EmptyWeatherResponseException()
    }
}