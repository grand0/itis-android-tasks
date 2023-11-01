package ru.kpfu.itis.ponomarev.androidcourse.model

import java.io.Serializable
import java.util.Date

sealed class GifModel

object GifButtonModel : GifModel()

data class GifDateModel(
    val date: Date,
) : GifModel()

data class GifCardModel(
    val id: Int,
    val url: String,
    val description: String,
    val tags: List<String>,
    val isLiked: Boolean = false,
) : GifModel(), Serializable
