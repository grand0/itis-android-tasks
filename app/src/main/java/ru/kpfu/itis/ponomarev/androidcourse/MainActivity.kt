package ru.kpfu.itis.ponomarev.androidcourse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ActivityMainBinding
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

//    private val messagesViewModel: MessagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if (supportFragmentManager.findFragmentByTag(MainFragment.MAIN_FRAGMENT_TAG) == null) {
            Logger.getLogger(this.javaClass.name).info("Adding main fragment")
            supportFragmentManager.beginTransaction()
                .add(binding.mainFragmentContainer.id, MainFragment(), MainFragment.MAIN_FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
