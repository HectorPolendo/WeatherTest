package com.hectorpolendo.weathertest.presentation.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hectorpolendo.weathertest.R
import com.hectorpolendo.weathertest.data.datasource.local.WeatherLocalDataSource
import com.hectorpolendo.weathertest.data.datasource.remote.WeatherRemoteDataSource
import com.hectorpolendo.weathertest.data.repository.WeatherRepositoryImpl
import com.hectorpolendo.weathertest.databinding.ActivityMainBinding
import com.hectorpolendo.weathertest.domain.usecase.GetWeatherUseCase
import com.hectorpolendo.weathertest.presentation.viewmodel.WeatherViewModel
import com.hectorpolendo.weathertest.presentation.viewmodel.WeatherViewModelFactory
import com.hectorpolendo.weathertest.utils.RetrofitInstance
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val api = RetrofitInstance.api
        val remoteDataSource = WeatherRemoteDataSource(api, "3bf0c3f98c56bd5c27e55321182c74c5")
        val localDataSource = WeatherLocalDataSource(applicationContext)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val useCase = GetWeatherUseCase(repository)
        val factory = WeatherViewModelFactory(useCase)


        viewModel = viewModels<WeatherViewModel> { factory }.value


        viewModel.weather.observe(this) { weather ->
            binding.tvCityCont.text = weather.city
            binding.tvTempCont.text = "${weather.temp}°C"
            binding.tvDescCont.text = weather.desc
        }

        binding.btnSearch.setOnClickListener {
            val city = binding.searchEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                viewModel.loadWeather(city)
                moveMapToCity(city)
            }
            hideKeyboard()
        }
    }

    private fun setDefaultCityFromLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val city = addresses[0].locality ?: addresses[0].subAdminArea ?: getString(R.string.unknown_city)
                            viewModel.loadWeather(city)
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(location.latitude, location.longitude),
                                    12f
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableUserLocation()
        setDefaultCityFromLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }
    }

    private fun moveMapToCity(cityName: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(cityName, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                map.clear()
                map.addMarker(MarkerOptions().position(latLng).title(cityName))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            } else {
                Toast.makeText(this, getString(R.string.not_found), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_search), Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}