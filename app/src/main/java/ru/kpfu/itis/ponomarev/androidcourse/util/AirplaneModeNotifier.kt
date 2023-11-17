package ru.kpfu.itis.ponomarev.androidcourse.util

import android.content.Context
import android.provider.Settings

object AirplaneModeNotifier {
    private val callbacks = mutableMapOf<Int, (Boolean) -> Unit>()
    private var index = 0

    fun registerCallback(context: Context, callback: (Boolean) -> Unit): Int {
        callbacks[index] = callback
        callback.invoke(isAirplaneModeOn(context))
        return index++
    }

    fun unregisterCallback(index: Int) {
        callbacks.remove(index)
    }

    fun notify(context: Context) {
        val isOn = isAirplaneModeOn(context)
        callbacks.values.forEach {
            it.invoke(isOn)
        }
    }

    fun isAirplaneModeOn(context: Context) = Settings.Global.getInt(
        context.contentResolver,
        Settings.Global.AIRPLANE_MODE_ON,
        0,
    ) != 0
}
