package com.example.latihanapi_googlemaps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

var locationArray = ArrayList<LocationModel>()
data class LocationModel(
        var id:Int,
        val tag:String,
        val latlong : LatLng,
        val marker : Marker
)