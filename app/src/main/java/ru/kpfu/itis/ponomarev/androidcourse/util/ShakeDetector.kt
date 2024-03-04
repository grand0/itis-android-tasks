package ru.kpfu.itis.ponomarev.androidcourse.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject
import kotlin.math.sqrt

class ShakeDetector @Inject constructor() : SensorEventListener {

    private var onShakeListener: ((Int) -> Unit)? = null
    private var shakeTimestamp = 0L
    private var shakeCount = 0

    fun setOnShake(listener: (count: Int) -> Unit) {
        onShakeListener = listener
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && onShakeListener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }
                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0
                }
                shakeTimestamp = now
                shakeCount++
                onShakeListener?.let { it(shakeCount) }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 1000
    }
}