package com.hectorpolendo.weathertest.data.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.hectorpolendo.weathertest.domain.model.WeatherEntity
import androidx.core.content.edit

class WeatherLocalDataSource(context: Context) {
    private val prefs = context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWeather(city: String, weather: WeatherEntity) {
        prefs.edit() {
            putString(city, gson.toJson(weather))
        }
    }

    fun getWeather(city: String): WeatherEntity? {
        val json = prefs.getString(city, null)
        return json?.let { gson.fromJson(it, WeatherEntity::class.java) }
    }
}