package ru.kpfu.itis.ponomarev.androidcourse.util

import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel

object UrlProvider {

    fun getIconUrlForWeather(model: WeatherModel): String {
        return "https://openweathermap.org/img/wn/${model.weather.first().icon}@2x.png"
    }
}