package ru.kpfu.itis.ponomarev.androidcourse.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel

interface OpenWeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
    ): WeatherModel?
}