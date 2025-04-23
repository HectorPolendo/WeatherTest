package com.hectorpolendo.weathertest.data.repository

import com.hectorpolendo.weathertest.data.datasource.local.WeatherLocalDataSource
import com.hectorpolendo.weathertest.data.datasource.remote.WeatherRemoteDataSource
import com.hectorpolendo.weathertest.domain.model.WeatherEntity
import com.hectorpolendo.weathertest.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override suspend fun getWeather(city: String): WeatherEntity {
        return try {
            val weather = remoteDataSource.getWeatherByCity(city)
            localDataSource.saveWeather(city, weather)
            weather
        } catch (e: Exception) {
            localDataSource.getWeather(city) ?: throw e
        }
    }
}