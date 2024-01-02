package ru.kpfu.itis.ponomarev.androidcourse.model

import java.io.Serializable

data class FilmModel(
    var id: Int = 0,
    var imageUrl: String,
    var title: String,
    var description: String,
    var year: Int,
    var rating: Double = 0.0,
    var ratesCount: Int = 0,
    var isFavorite: Boolean = false,
) : Serializable
