package com.example.latihanapi_googlemaps

import android.location.Location
import com.google.android.gms.maps.model.LatLng

////define latitude and longitude
//val perpus = LatLng(-7.2817, 112.7955)
//val graha = LatLng(-7.2769, 112.7916)
//
//val manarulLocation = Location("")
//manarulLocation.latitude = -7.2824
//manarulLocation.longitude = 112.7931
//val manarul = LatLng(manarulLocation.latitude, manarulLocation.longitude)
//
//val elektroLocation = Location("")
//elektroLocation.latitude = -7.2851
//elektroLocation.longitude = 112.7961
//val elektro = LatLng(elektroLocation.latitude, elektroLocation.longitude)
//
////add distances
//val distManarulElektro = manarulLocation.distanceTo(elektroLocation)
//
////add marker to maps
//mMap.addMarker(MarkerOptions().position(manarul).title("Manarul Ilmi to Elektro ($distManarulElektro)"))
//mMap.addMarker(MarkerOptions().position(elektro).title("Teknik Elektro"))
//mMap.addMarker(MarkerOptions().position(perpus).title("Perpustakaan ITS"))
//mMap.addMarker(MarkerOptions().position(graha).title("Graha Sepuluh Nopember"))
//
////when app runs first time
////mMap.moveCamera(CameraUpdateFactory.newLatLng(manarul))
//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manarul, zoomScale))
//
////draw line and circle
//mMap.addPolyline(PolylineOptions()
//.clickable(true)
//.add(elektro, manarul)
//.color(R.color.teal_700))
//mMap.addPolyline(PolylineOptions()
//.clickable(true)
//.add(graha, perpus)
//.color(R.color.teal_700))
//mMap.addCircle(CircleOptions()
//.center(elektro)
//.radius(100.0) //meter
//.strokeColor(R.color.transparent)
//.fillColor(R.color.teal_200))