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
        val client = createUnsafeClient().addInterceptor { chain ->
            val url = chain.request().url().newBuilder()
                .addQueryParameter(Keys.APP_ID_KEY, BuildConfig.OPENWEATHER_API_KEY)
                .addQueryParameter(Keys.UNITS_KEY, "metric")
                .build()

            val req = chain.request()
                .newBuilder()
                .url(url)
                .build()

            chain.proceed(req)
        }.build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPENWEATHER_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }

    private fun createUnsafeClient(): OkHttpClient.Builder {
        val okHttpClient = OkHttpClient.Builder()
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            if (trustAllCerts.isNotEmpty() && trustAllCerts.first() is X509TrustManager) {
                okHttpClient.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts.first() as X509TrustManager
                )
                okHttpClient.hostnameVerifier { _, _ -> true }
            }

            return okHttpClient
        } catch (e: Exception) {
            return okHttpClient
        }
    }
}