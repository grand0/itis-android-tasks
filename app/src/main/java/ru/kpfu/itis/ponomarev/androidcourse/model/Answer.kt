package ru.kpfu.itis.ponomarev.androidcourse.model

import java.io.Serializable

data class Answer(
    val answer: Int,
    var checked: Boolean = false
) : Serializable {
    override fun toString(): String {
        return "$answer"
    }
}
