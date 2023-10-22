package ru.kpfu.itis.ponomarev.androidcourse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ActivityMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.ui.StartFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, StartFragment())
            .commit()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
