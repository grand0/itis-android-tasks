package ru.kpfu.itis.ponomarev.androidcourse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ActivityMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.db.AppDatabase
import ru.kpfu.itis.ponomarev.androidcourse.ui.fragment.HomeFragment
import ru.kpfu.itis.ponomarev.androidcourse.ui.fragment.LoginFragment
import ru.kpfu.itis.ponomarev.androidcourse.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        AppDatabase.init(this)
        AppConfig.init(this)
        AppConfig.restoreConfig()

        val startFragment = if (AppConfig.userEmail == null || AppConfig.userPasswordHash == null) {
            LoginFragment()
        } else {
            HomeFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                startFragment
            )
            .commit()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
