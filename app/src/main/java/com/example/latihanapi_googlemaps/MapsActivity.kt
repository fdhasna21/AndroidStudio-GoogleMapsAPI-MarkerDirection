package com.example.latihanapi_googlemaps

import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val zoomScale : Float = 15f
        var x = 0
        var location1 = Location("")
        var location2 = Location("")
        var latlng1 :LatLng
        var latlng2 :LatLng

        val surabaya = LatLng(-7.2575, 112.7521)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surabaya, zoomScale))

        mMap.setOnCameraIdleListener {
            val latlng : LatLng = mMap.cameraPosition.target
            val geocoder = Geocoder(this)
            geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
        }

        if(x==0){
            noLocationAvailable.visibility = View.VISIBLE
            location1_container.visibility = View.GONE
            location2_container.visibility = View.GONE
            distance_container.visibility = View.GONE
        }

        btn_addLocation.setOnClickListener {
            x++
            val latlng : LatLng = mMap.cameraPosition.target
            val geocoder = Geocoder(this)
            geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

            if(x==1){
                noLocationAvailable.visibility = View.INVISIBLE
                location1_container.visibility = View.VISIBLE
            }
            else{
                location2_container.visibility = View.VISIBLE
                distance_container.visibility = View.VISIBLE
            }

            if(x%2 == 0){
                latlng2 = latlng
                var marker1 = mMap.addMarker(MarkerOptions().position(latlng2).title("Location 1"))
                location2.longitude = latlng2.longitude
                location2.latitude = latlng2.latitude
                location2_location.text = """Long : ${latlng2.longitude}
                                            |Lang : ${latlng2.latitude}""".trimMargin()
            }
            else{
                latlng1 = latlng
                var marker2 = mMap.addMarker(MarkerOptions().position(latlng1).title("Location 2"))
                location1.longitude = latlng1.longitude
                location1.latitude = latlng1.latitude
                location1_location.text = """Long : ${latlng1.longitude}
                                            |Lang : ${latlng1.latitude}""".trimMargin()
            }
            distance.text = location1.distanceTo(location2).toString()
            Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()

            //TODO : remove previous marker, add polygon, add title above marker, add reset button (delete all record)
        }


        //add zoo button
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }
}