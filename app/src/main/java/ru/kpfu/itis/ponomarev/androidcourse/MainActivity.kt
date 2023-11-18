package ru.kpfu.itis.ponomarev.androidcourse

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ActivityMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier.notify
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationsUtil

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

    private var job: Job? = null
    private var unfinishedCoroutines = 0
    private var stopOnBackground = false

    @OptIn(NavigationUiSaveStateControl::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val navController =
            (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment)
                .navController

        NavigationUI.setupWithNavController(binding.bnvMain, navController, false)

        val action = intent.getIntExtra("action", NO_ACTION)
        onIntentAction(action)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationsPermissionWithRationale()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtil.createNotificationChannels(this)
        }

        val intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                notify(context)
            }
        }
        registerReceiver(receiver, intentFilter)
        airplaneModeCallbackId = AirplaneModeNotifier.registerCallback(this) { isOn ->
            binding.overlayNoConnection.isGone = !isOn
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action = intent?.getIntExtra("action", NO_ACTION) ?: NO_ACTION
        onIntentAction(action)
    }

    private fun onIntentAction(action: Int) {
        when (action) {
            ACTION_SHOW_MESSAGE -> {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.message_snackbar),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
            ACTION_SHOW_SETTINGS -> {
                (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment)
                    .navController
                    .apply {
                        if (currentDestination?.id != R.id.notificationSettingsFragment) {
                            popBackStack(R.id.mainFragment, false)
                            navigate(R.id.action_mainFragment_to_notificationSettingsFragment)
                        }
                    }
            }
        }
    }

    private fun requestNotificationsPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Snackbar.make(binding.root, R.string.notifications_permission_rationale, Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_btn_text) { requestNotificationsPermission() }
                .show()
        } else {
            requestNotificationsPermission()
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATIONS_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allowed = false
        when (requestCode) {
            NOTIFICATIONS_PERMISSION_REQUEST_CODE -> {
                allowed = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (!allowed) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                requestNotificationsPermissionWithRationale()
            } else {
                requestOpenApplicationSettings()
            }
        }
    }

    private fun requestOpenApplicationSettings() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.allow_notifications_text)
            .setMessage(R.string.notifications_permission_settings_rationale)
            .setPositiveButton(R.string.open_settings_btn_text) { _, _ ->
                openApplicationSettings()
            }

        builder.create().show()
    }

    private fun openApplicationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        startActivity(intent)
    }

    fun startCoroutines(n: Int, async: Boolean, stopOnBackground: Boolean) {
        unfinishedCoroutines = n
        this.stopOnBackground = stopOnBackground
        job = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repeat (n) {
                    if (async) {
                        launch { startCoroutine(it + 1, n) }
                    } else {
                        startCoroutine(it + 1, n)
                    }
                }
            }
        }.also {
            it.invokeOnCompletion { cause ->
                if (cause == null) {
                    NotificationsUtil.sendCoroutinesFinishedNotification(this)
                } else if (cause is CancellationException) {
                    Log.e(javaClass.name, "Cancelled $unfinishedCoroutines coroutine(s).")
                }
                job = null
            }
        }
    }

    private suspend fun startCoroutine(index: Int, total: Int) {
        delay(3000)
        Log.e(javaClass.name, "Coroutine $index/$total finished.")
        unfinishedCoroutines--
    }

    override fun onStop() {
        if (stopOnBackground) {
            job?.cancel(
                "User left the app."
            )
        }
        super.onStop()
    }

    override fun onDestroy() {
        _binding = null
        airplaneModeCallbackId?.let {
            AirplaneModeNotifier.unregisterCallback(it)
        }
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATIONS_PERMISSION_REQUEST_CODE = 1

        const val NO_ACTION = 0
        const val ACTION_SHOW_MESSAGE = 1
        const val ACTION_SHOW_SETTINGS = 2
    }
}
