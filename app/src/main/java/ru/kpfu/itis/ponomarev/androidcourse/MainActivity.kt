package ru.kpfu.itis.ponomarev.androidcourse

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
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
import ru.kpfu.itis.ponomarev.androidcourse.util.CoroutinesSettings
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationsUtil
import ru.kpfu.itis.ponomarev.androidcourse.util.setIcon
import ru.kpfu.itis.ponomarev.androidcourse.util.setOnSurfaceTint

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

    private var job: Job? = null
    private var unfinishedCoroutines = 0
    private var stopOnBackground = false

    private var justChangedTheme = false

    @OptIn(NavigationUiSaveStateControl::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val prefs = getPreferences(Context.MODE_PRIVATE)
        if (prefs.contains(getString(R.string.night_mode_key))) {
            val nightMode = prefs.getBoolean(getString(R.string.night_mode_key), false)
            AppCompatDelegate.setDefaultNightMode(
                if (nightMode)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )
            justChangedTheme = savedInstanceState?.getBoolean(getString(R.string.just_changed_theme_key), false) ?: false
        }

        val navController =
            (supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment)
                .navController

        NavigationUI.setupWithNavController(binding.bnvMain, navController, false)

        if (!justChangedTheme) {
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
        }
        airplaneModeCallbackId = AirplaneModeNotifier.registerCallback(this) { isOn ->
            binding.overlayNoConnection.isGone = !isOn
            if (isOn) (binding.vNoConnection.background as? AnimatedVectorDrawable)?.start()
        }
        binding.vNoConnection.setOnClickListener {
            (it.background as? AnimatedVectorDrawable)?.start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        menu?.findItem(R.id.action_night_mode)?.apply {
            val icon: Drawable?
            if (justChangedTheme) {
                icon = AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                        R.drawable.avd_light_to_night
                    else
                        R.drawable.avd_night_to_light
                )
            } else {
                icon = AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                        R.drawable.baseline_dark_mode_24
                    else
                        R.drawable.baseline_light_mode_24
                )
            }
            icon?.setOnSurfaceTint(this@MainActivity)
            this.icon = icon
            if (justChangedTheme) {
                (icon as? AnimatedVectorDrawable)?.start()
                justChangedTheme = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_night_mode -> {
            justChangedTheme = true
            airplaneModeCallbackId?.let { AirplaneModeNotifier.unregisterCallback(it) }
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                with (getPreferences(Context.MODE_PRIVATE).edit()) {
                    putBoolean(getString(R.string.night_mode_key), false)
                    commit()
                }

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                with (getPreferences(Context.MODE_PRIVATE).edit()) {
                    putBoolean(getString(R.string.night_mode_key), true)
                    commit()
                }

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.just_changed_theme_key), justChangedTheme)
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
            val icon = AppCompatResources.getDrawable(this, R.drawable.avd_notifications_off)
            icon?.setOnSurfaceTint(this)

            Snackbar.make(binding.root, R.string.notifications_permission_rationale, 5000)
                .setAction(R.string.allow_btn_text) { requestNotificationsPermission() }
                .setIcon(icon)
                .addCallback(object : BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        (icon as? AnimatedVectorDrawable)?.start()
                    }
                })
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
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.allow_notifications_text)
            .setIcon(AppCompatResources.getDrawable(this, R.drawable.baseline_notifications_24)?.apply {
                setOnSurfaceTint(this@MainActivity)
            })
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

    fun startCoroutines() {
        unfinishedCoroutines = CoroutinesSettings.count
        this.stopOnBackground = CoroutinesSettings.stopOnBackground
        job = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repeat (CoroutinesSettings.count) {
                    if (CoroutinesSettings.async) {
                        launch { startCoroutine(it + 1, CoroutinesSettings.count) }
                    } else {
                        startCoroutine(it + 1, CoroutinesSettings.count)
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
