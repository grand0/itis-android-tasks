package ru.kpfu.itis.ponomarev.androidcourse.domain.repository

import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel

interface WeatherRepository {

    suspend fun getCurrentWeather(city: String): WeatherModel
}