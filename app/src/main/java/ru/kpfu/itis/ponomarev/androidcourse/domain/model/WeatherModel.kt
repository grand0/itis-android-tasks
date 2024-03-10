package ru.kpfu.itis.ponomarev.androidcourse.domain.model

import com.google.gson.annotations.SerializedName

// maybe it is a better idea to separate models for different layers but this way is fine too ig
data class WeatherModel(
    val weather: List<WeatherDataModel>,
    val main: MainDataModel,
    val sys: SysDataModel,
    @SerializedName("name") val city: String,
)

data class WeatherDataModel(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

data class MainDataModel(
    val temp: Double,
)

data class SysDataModel(
    val country: String,
)
