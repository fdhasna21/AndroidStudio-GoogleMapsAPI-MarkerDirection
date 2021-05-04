package com.example.latihanapi_googlemaps

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.latihanapi_googlemaps.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_mark_location.*

class MarkLocation : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var actionBarToggle : ActionBarDrawerToggle

    fun showAllLocation(){
        ml_recycler.layoutManager = LinearLayoutManager(this)
        ml_recycler.adapter = LocationAdapter(this, locationArray)
        if(locationArray.size == 0){
            ml_noLocationAvailable.visibility = View.VISIBLE
            ml_recycler.visibility = View.GONE
            ml_divider.visibility = View.GONE
            ml_direction_container.visibility = View.GONE
        }
        else{
            ml_noLocationAvailable.visibility = View.GONE
            ml_recycler.visibility = View.VISIBLE
            ml_divider.visibility = View.VISIBLE
            ml_direction_container.visibility = View.VISIBLE
        }
        spinnerSetting()
    }

    fun deleteLocation(location: LocationModel){
        val builder = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure?")
        builder.setCancelable(false)
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Yes"){ dialog: DialogInterface, which ->
            locationArray.removeAt(location.id)
            Toast.makeText(this, "Delete location ${location.id + 1}.", Toast.LENGTH_SHORT).show()
            location.marker.remove()
            dialog.dismiss()
            showAllLocation()
        }

        builder.setNegativeButton("No"){ dialog: DialogInterface, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun closeKeyboard(){
        val view = this.currentFocus
        if(view!=null){
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun spinnerSetting(){
        val spinnerArray = ArrayList<String>()
        for(i in 1 until locationArray.size+1){
            spinnerArray.add("Location $i")
        }
        ml_sp_locationA.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
        ml_sp_locationB.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerArray)
    }

    private fun distance(locationA: LatLng, locationB: LatLng){
        val LocationA = Location("")
        val LocationB = Location("")
        LocationA.longitude = locationA.longitude
        LocationA.latitude = locationA.latitude
        LocationB.longitude = locationB.longitude
        LocationB.latitude = locationB.latitude
        ml_distanceValue.text = (LocationA.distanceTo(LocationB)/1000).toString() + " km"
        //TODO : distance.text didn't change
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.ml_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        showAllLocation()
        ml_distanceValue.text = "distanceValue"

        actionBarToggle = ActionBarDrawerToggle(this, ml_drawerLayout, 0, 0)
        ml_drawerLayout.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mark Location"
        actionBarToggle.syncState()
        ml_navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.drawMenu_findDistance -> {
                    val intent = Intent(this, FindDistance::class.java)
                    startActivity(intent)
                    true
                }
                R.id.drawMenu_markLocation -> {
                    true
                }
                else -> false
            }
        }

        if(locationArray.size > 0) {
            var locationA = LocationAdapter(this@MarkLocation, locationArray).getLatLong(0)
            var locationB = LocationAdapter(this@MarkLocation, locationArray).getLatLong(0)
            ml_sp_locationA.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    locationA = LocationAdapter(this@MarkLocation, locationArray).getLatLong(position)
                    distance(locationA, locationB)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    locationA = LocationAdapter(this@MarkLocation, locationArray).getLatLong(0)
                    distance(locationA, locationB)
                }
            }

            ml_sp_locationB.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    locationB =
                        LocationAdapter(this@MarkLocation, locationArray).getLatLong(position)
                    distance(locationA, locationB)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    locationB = LocationAdapter(this@MarkLocation, locationArray).getLatLong(0)
                    distance(locationA, locationB)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        val zoomScale : Float = 15f
        val surabaya = LatLng(-7.2575, 112.7521)
        var currentPosition = surabaya

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surabaya, zoomScale))
        var currentMarker : Marker = mMap.addMarker(
                MarkerOptions()
                        .position(currentPosition)
                        .title("Current")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

        mMap.setOnCameraIdleListener {
            currentMarker.remove()
            val latlng : LatLng = mMap.cameraPosition.target
            val geocoder = Geocoder(this)
            geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            currentMarker = mMap.addMarker(MarkerOptions().position(latlng)
                    .title("Current")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            showAllLocation()
        }

        ml_btn_addLocation.setOnClickListener {
            val builder = Dialog(this, R.style.Theme_Dialog)
            builder.setCancelable(false)
            builder.setContentView(R.layout.dialog_add_tag)
            val inputTag = builder.findViewById<EditText>(R.id.dialog_inputTag)
            val btnCancel = builder.findViewById<TextView>(R.id.dialog_btnCancel)
            val btnConfirm = builder.findViewById<TextView>(R.id.dialog_btnConfirm)

            btnCancel.setOnClickListener{
                builder.dismiss()
            }

            btnConfirm.setOnClickListener {
                val tag = inputTag.text.toString()
                if(tag == ""){
                    Toast.makeText(this@MarkLocation, "Label cannot be empty.", Toast.LENGTH_SHORT).show()

                }
                else{
                    val latlng : LatLng = mMap.cameraPosition.target
                    val geocoder = Geocoder(this)
                    geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
                    locationArray.add(LocationModel(0, tag, latlng, mMap.addMarker(MarkerOptions().position(latlng))))
                    builder.dismiss()
                    showAllLocation()
                    closeKeyboard()
                }

                //TODO : recycler didn't updated, just shown first locations because of softkeyboard stuck
            }
            builder.show()
        }

        ml_btn_reset.setOnClickListener {
            locationArray.clear()
            mMap.clear()
            currentPosition = mMap.cameraPosition.target
            currentMarker = mMap.addMarker(
                    MarkerOptions()
                            .position(currentPosition)
                            .title("Current")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            Toast.makeText(this@MarkLocation, "Reset all locations.", Toast.LENGTH_SHORT).show()
            showAllLocation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(ml_drawerLayout.isDrawerOpen(GravityCompat.START)){
            ml_drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            ml_drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.beginTransaction().remove(ml_map).commit()
    }
}