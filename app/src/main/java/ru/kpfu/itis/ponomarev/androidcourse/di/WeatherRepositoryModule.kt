package ru.kpfu.itis.ponomarev.androidcourse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kpfu.itis.ponomarev.androidcourse.data.repository.WeatherRepositoryImpl
import ru.kpfu.itis.ponomarev.androidcourse.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherRepositoryModule {

    @Binds
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}