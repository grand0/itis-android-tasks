package ru.kpfu.itis.ponomarev.androidcourse.util

import android.app.Notification

enum class Visibility(val text: String, val id: Int) {
    PUBLIC("Public", Notification.VISIBILITY_PUBLIC),
    SECRET("Secret", Notification.VISIBILITY_SECRET),
    PRIVATE("Private", Notification.VISIBILITY_PRIVATE);

    override fun toString() = text
}
