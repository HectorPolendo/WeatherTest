package com.hectorpolendo.weathertest.data.datasource.remote

import com.hectorpolendo.weathertest.data.datasource.service.WeatherApi
import com.hectorpolendo.weathertest.data.model.toEntity
import com.hectorpolendo.weathertest.domain.model.WeatherEntity

class WeatherRemoteDataSource(
    private val api: WeatherApi,
    private val apiKey: String
) {

    suspend fun getWeatherByCity(city: String): WeatherEntity {
        val response = api.getWeatherByCity(city, apiKey)
        return response.toEntity()
    }
}