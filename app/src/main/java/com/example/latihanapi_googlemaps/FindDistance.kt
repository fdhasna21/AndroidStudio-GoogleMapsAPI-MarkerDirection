package com.example.latihanapi_googlemaps

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
import kotlinx.android.synthetic.main.activity_find_distance.*


class FindDistance : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var actionBarToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_distance)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.fd_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Find Distance"
        actionBarToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.drawMenu_findDistance -> {
                    true
                }
                R.id.drawMenu_markLocation -> {
                    val intent = Intent(this, MarkLocation::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        val zoomScale : Float = 15f
        var noLocation : Boolean = true
        var updateLocationA : Boolean = true //true->LocationA, false->LocationB
        val surabaya = LatLng(-7.2575, 112.7521)
        var currentPosition = surabaya

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surabaya, zoomScale))
        var currentMarker : Marker  = mMap.addMarker(
            MarkerOptions()
                .position(currentPosition)
                .title("Current")
                .icon(BitmapDescriptorFactory.defaultMarker(HUE_AZURE))
        )

        val locationA = Location("")
        val locationB = Location("")
        var latlngA = currentPosition
        var latlngB = currentPosition
        var markerA = currentMarker
        var markerB = currentMarker
        var distancePolyline : Polyline= mMap.addPolyline(
            PolylineOptions().add(latlngA, latlngB).color(
                R.color.teal_700
            )
        )

        mMap.setOnCameraIdleListener {
            currentMarker.remove()
            val latlng : LatLng = mMap.cameraPosition.target
            val geocoder = Geocoder(this)
            geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            currentMarker = mMap.addMarker(
                MarkerOptions().position(latlng).title("Current").icon(
                    BitmapDescriptorFactory.defaultMarker(
                        HUE_AZURE
                    )
                )
            )
        }

        fd_btn_addLocation.setOnClickListener {
            //TODO : respond if location change (want to change hint and border color but it can't, dunno why)
            val latlng : LatLng = mMap.cameraPosition.target
            val latlngString = "${latlng.longitude} | ${latlng.latitude}"
            val geocoder = Geocoder(this)
            val geoAddress = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            val geoResult = geoAddress[0].getAddressLine(0)
            var updateLocation : String = ""

            if(noLocation){
                fd_noLocationAvailable.visibility = View.INVISIBLE
                fd_locationA_container.visibility = View.VISIBLE
            }
            else{
                fd_locationB_container.visibility = View.VISIBLE
                fd_distance_container.visibility = View.VISIBLE
            }

            if(updateLocationA){
                updateLocation = "Location A"
                latlngA = latlng
                markerA.isVisible = true
                markerA.remove()
                markerA = this.mMap.addMarker(
                        MarkerOptions()
                                .position(latlngA).title("Location 1")
                                .snippet(latlngString)
                )
                locationA.longitude = latlngA.longitude
                locationA.latitude = latlngA.latitude
                fd_locationA_longitude.setText(locationA.longitude.toString())
                fd_locationA_latitude.setText(locationA.latitude.toString())
                fd_locationA_address.text = geoResult.toString()
                updateLocationA = false
                noLocation = false
            }
            else{
                updateLocation = "Location B"
                latlngB = latlng
                markerB.isVisible = true
                markerB.remove()
                markerB = mMap.addMarker(
                        MarkerOptions()
                                .position(latlngB)
                                .title("Location 2")
                                .snippet(latlngString)
                )
                locationB.longitude = latlngB.longitude
                locationB.latitude = latlngB.latitude
                fd_locationB_longitude.setText(locationB.longitude.toString())
                fd_locationB_latitude.setText(locationB.latitude.toString())
                fd_locationB_address.text = geoResult.toString()
                updateLocationA = true
            }
            Toast.makeText(applicationContext, "$updateLocation updated.", Toast.LENGTH_SHORT).show()

            if((latlngA.toString() != currentPosition.toString()) && (latlngB.toString() != currentPosition.toString()))
            {
                distancePolyline.remove()
                distancePolyline = mMap.addPolyline(
                    PolylineOptions()
                        .add(latlngA, latlngB)
                        .color(R.color.teal_700)
                )
            }
            fd_distance.setText((locationA.distanceTo(locationB)/1000).toString() + " km")
        }

        fd_btn_reset.setOnClickListener {
            if(noLocation==false){Toast.makeText(this, "Reset all locations.", Toast.LENGTH_SHORT).show()}
            noLocation = true
            updateLocationA = true
            distancePolyline.remove()
            currentPosition = mMap.cameraPosition.target
            latlngA = currentPosition
            latlngB = currentPosition
            markerA.isVisible=false
            markerB.isVisible=false
            distancePolyline.isVisible = false
            mMap.clear()
            fd_noLocationAvailable.visibility = View.VISIBLE
            fd_locationA_container.visibility = View.GONE
            fd_locationB_container.visibility = View.GONE
            fd_distance_container.visibility = View.GONE
            currentMarker = mMap.addMarker(
                MarkerOptions()
                    .position(currentPosition)
                    .title("Current")
                    .icon(BitmapDescriptorFactory.defaultMarker(HUE_AZURE))
            )
        }

        fd_btn_reset.callOnClick()
    }

    override fun onSupportNavigateUp(): Boolean {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

}