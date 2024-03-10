package ru.kpfu.itis.ponomarev.androidcourse.di

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.kpfu.itis.ponomarev.androidcourse.BuildConfig
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.di.qualifier.DebugInfoMap

@Module
@InstallIn(ActivityComponent::class)
object DebugScreenModule {

    // maybe it's overkill to do this with di, idk
    @DebugInfoMap
    @Provides
    fun provideDebugInfoMap(
        @ApplicationContext context: Context,
    ): Map<String, String> {
        val infoMap = mutableMapOf<String, String>()
        infoMap["app_name"] = context.getString(R.string.app_name)
        infoMap["base_url"] = BuildConfig.OPENWEATHER_API_BASE_URL
        infoMap["version_name"] = BuildConfig.VERSION_NAME
        infoMap["version_code"] = BuildConfig.VERSION_CODE.toString()
        infoMap["device"] = "${Build.MANUFACTURER} ${Build.MODEL}"
        infoMap["os"] = String.format("Android %s (API %d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)
        return infoMap
    }
}