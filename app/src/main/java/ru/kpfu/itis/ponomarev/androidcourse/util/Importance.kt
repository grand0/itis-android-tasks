package ru.kpfu.itis.ponomarev.androidcourse.util

import android.app.NotificationManager

enum class Importance(val text: String, val id: Int) {
    MEDIUM("Medium", NotificationManager.IMPORTANCE_LOW),
    HIGH("High", NotificationManager.IMPORTANCE_DEFAULT),
    URGENT("Urgent", NotificationManager.IMPORTANCE_HIGH);

    override fun toString() = text
}
