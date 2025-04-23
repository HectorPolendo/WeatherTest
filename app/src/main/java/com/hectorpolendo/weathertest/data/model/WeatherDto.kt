package com.hectorpolendo.weathertest.data.model

import com.hectorpolendo.weathertest.domain.model.WeatherEntity

data class WeatherDto(
    val name: String,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>
)

fun WeatherDto.toEntity(): WeatherEntity {
    return WeatherEntity(
        city = name,
        temp = main.temp,
        desc = weather.firstOrNull()?.description ?: "No description"
    )
}