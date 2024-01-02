package ru.kpfu.itis.ponomarev.androidcourse.event

import ru.kpfu.itis.ponomarev.androidcourse.model.FilmModel

typealias FilmChangedListener = (FilmModel) -> Unit

object FilmChangedEventManager {

    private val listeners = mutableMapOf<Int, FilmChangedListener>()
    private var curIndex = 0

    fun addListener(listener: FilmAddedListener): CancellationToken {
        val key = curIndex++
        listeners[key] = listener
        return object : CancellationToken {
            override fun cancel() {
                listeners.remove(key)
            }
        }
    }

    fun notifyEvent(film: FilmModel) {
        listeners.values.forEach { it.invoke(film) }
    }

    interface CancellationToken {
        fun cancel()
    }
}