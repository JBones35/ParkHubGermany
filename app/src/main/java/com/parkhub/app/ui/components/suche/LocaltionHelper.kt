package com.parkhub.app.ui.components.suche

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onLocation: (latitude: Double, longitude: Double) -> Unit
) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocation(location.latitude, location.longitude)
        } else {
            onLocation(49.0069, 8.4037)
        }
    }
}