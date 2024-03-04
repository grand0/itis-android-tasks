package ru.kpfu.itis.ponomarev.androidcourse.di

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kpfu.itis.ponomarev.androidcourse.BuildConfig
import ru.kpfu.itis.ponomarev.androidcourse.data.remote.OpenWeatherApi
import ru.kpfu.itis.ponomarev.androidcourse.util.Keys
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object OpenWeatherApiModule {

    @Provides
    fun provideOpenWeatherApi(): OpenWeatherApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain.request().url().newBuilder()
                    .addQueryParameter(Keys.APP_ID_KEY, BuildConfig.OPENWEATHER_API_KEY)
                    .addQueryParameter(Keys.UNITS_KEY, "metric")
                    .build()

                val req = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                chain.proceed(req)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPENWEATHER_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }
}