package com.hectorpolendo.weathertest.domain.repository

import com.hectorpolendo.weathertest.domain.model.WeatherEntity

interface WeatherRepository {
    suspend fun getWeather(city: String): WeatherEntity
}