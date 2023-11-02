package ru.kpfu.itis.ponomarev.androidcourse.util

import android.graphics.Color
import android.os.Build
import kotlin.random.Random

object ColorGenerator {
    fun generateForString(s: String): Int {
        // Color.valueOf requires API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hash = s.hashCode()
            val rng = Random(hash)
            val color = when (rng.nextInt(1, 4)) {
                1 -> Color.valueOf(
                    rng.nextFloat(0.8f, 1.0f),
                    rng.nextFloat(0.6f, 0.8f),
                    rng.nextFloat(0.6f, 0.8f),
                )
                2 -> Color.valueOf(
                    rng.nextFloat(0.6f, 0.8f),
                    rng.nextFloat(0.8f, 1.0f),
                    rng.nextFloat(0.6f, 0.8f),
                )
                else -> Color.valueOf(
                    rng.nextFloat(0.6f, 0.8f),
                    rng.nextFloat(0.6f, 0.8f),
                    rng.nextFloat(0.8f, 1.0f),
                )
            }
            return color.toArgb()
        } else {
            return Color.GRAY
        }
    }

    private fun Random.nextFloat(from: Float, to: Float): Float {
        val num = this.nextFloat()
        return num * (to - from) + from
    }
}
