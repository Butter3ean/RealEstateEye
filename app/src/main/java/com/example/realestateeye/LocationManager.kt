package com.example.realestateeye

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.compose.runtime.MutableState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationManager() {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        city: MutableState<String>,
        coordinates: MutableState<LatLng>
    ) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    coordinates.value = LatLng(it.latitude, it.longitude)
                    getCurrentCity(context, coordinates.value, city)
                }
            }
    }

    private fun getCurrentCity(context: Context, coordinates: LatLng, city: MutableState<String>) {
        val geocoder = Geocoder(context)

        if (Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1) {
                city.value = it[0].locality.toString()
            }
        } else {
            val address = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
            if (!address.isNullOrEmpty()) {
                city.value = address[0].locality.toString()
            }
        }
    }
}