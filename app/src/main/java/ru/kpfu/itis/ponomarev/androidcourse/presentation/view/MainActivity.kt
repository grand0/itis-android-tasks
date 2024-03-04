package ru.kpfu.itis.ponomarev.androidcourse.presentation.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ActivityMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.presentation.view.fragment.DebugFragment
import ru.kpfu.itis.ponomarev.androidcourse.presentation.view.fragment.MainFragment
import ru.kpfu.itis.ponomarev.androidcourse.util.ShakeDetector
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var shakeDetector: ShakeDetector
    private var accelerometer: Sensor? = null
    private var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, MainFragment())
            .commit()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector.setOnShake { count ->
            if (supportFragmentManager.findFragmentByTag(DebugFragment.DEBUG_FRAGMENT_TAG) == null && count >= 2) {
                supportFragmentManager.beginTransaction()
                    .replace(binding.fragmentContainer.id, DebugFragment(), DebugFragment.DEBUG_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        sensorManager?.unregisterListener(shakeDetector)
        super.onPause()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
