package com.hectorpolendo.weathertest.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("cityText")
fun setCityText(view: TextView, city: String?) {
    view.text = city
}

@BindingAdapter("temperatureText")
fun setTemperatureText(view: TextView, temperature: Double?) {
    view.text = "${temperature}°C"
}

@BindingAdapter("descriptionText")
fun setDescriptionText(view: TextView, description: String?) {
    view.text = description
}