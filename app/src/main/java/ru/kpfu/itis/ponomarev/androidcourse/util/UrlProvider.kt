package ru.kpfu.itis.ponomarev.androidcourse.util

import ru.kpfu.itis.ponomarev.androidcourse.domain.model.WeatherModel

object UrlProvider {

    fun getIconUrlForWeather(model: WeatherModel): String {
        // i'm sure there is a better way to do this. oh well
        return "https://openweathermap.org/img/wn/${model.weather.first().icon}@2x.png"
    }
}