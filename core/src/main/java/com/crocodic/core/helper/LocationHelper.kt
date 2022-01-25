package com.crocodic.core.helper

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Created by @yzzzd on 4/19/18.
 */

object LocationHelper {

    fun LatLng.distanceTo(latLng: LatLng) = distance(this, latLng)

    fun distance(latLng: LatLng, latLng2: LatLng) =
        Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }.distanceTo(
            Location("").apply {
                latitude = latLng2.latitude
                longitude = latLng2.longitude
            }
        )/1000f
}