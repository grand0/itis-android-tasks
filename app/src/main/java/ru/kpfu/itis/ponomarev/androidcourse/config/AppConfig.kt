package ru.kpfu.itis.ponomarev.androidcourse.config

import android.content.Context
import android.content.SharedPreferences
import ru.kpfu.itis.ponomarev.androidcourse.util.Keys

object AppConfig {

    const val USER_DELETION_GRACE_PERIOD_MS = 7 * 24 * 60 * 60 * 1000

    private var prefs: SharedPreferences? = null

    var userEmail: String? = null
        private set
    var userPasswordHash: String? = null
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(Keys.APP_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun restoreConfig() {
        if (prefs == null) throw IllegalStateException("AppConfig was not initialized")

        prefs?.apply {
            userEmail = getString(Keys.USER_EMAIL_PREF, null)
            userPasswordHash = getString(Keys.USER_PASSWORD_HASH_PREF, null)
        }
    }

    fun rememberUser(email: String, passwordHash: String) {
        if (prefs == null) throw IllegalStateException("AppConfig was not initialized")

        userEmail = email
        userPasswordHash = passwordHash
        prefs?.edit()?.apply {
            putString(Keys.USER_EMAIL_PREF, email)
            putString(Keys.USER_PASSWORD_HASH_PREF, passwordHash)
            apply()
        }
    }

    fun clearUserConfig() {
        if (prefs == null) throw IllegalStateException("AppConfig was not initialized")

        userEmail = null
        userPasswordHash = null
        prefs?.edit()?.apply {
            putString(Keys.USER_EMAIL_PREF, null)
            putString(Keys.USER_PASSWORD_HASH_PREF, null)
            apply()
        }
    }
}
