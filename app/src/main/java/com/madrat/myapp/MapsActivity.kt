package com.madrat.myapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.madrat.myapp.databinding.ActivityMapsBinding

// Реализация этого решения
// https://www.geeksforgeeks.org/how-to-get-user-location-in-android/

class MapsActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_GET_LAST_LOCATION_ID = 44

        private const val PERMISSIONS_ALLOW_USING_LOCATION_ID = 100
    }

    lateinit var binding: ActivityMapsBinding

    lateinit var locationClient: FusedLocationProviderClient

    // Get the SupportMapFragment and request notification
    // when the map is ready to be used.
    lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        // Client initialization
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        // Map initialization
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // method to get the location
        getLastLocation()
    }
    // If everything is alright then
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_GET_LAST_LOCATION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission_group.LOCATION), PERMISSIONS_ALLOW_USING_LOCATION_ID
                    )
                } else {
                    locationClient.lastLocation.addOnCompleteListener { task ->
                        val location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            updateViewsWithLocation(location.latitude, location.longitude)
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on" +
                        " your location...", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
        }
    }
    private fun requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods
        val locationRequest = LocationRequest()

        // For a high level accuracy use PRIORITY_HIGH_ACCURACY argument.
        // For a low level accuracy (city), use PRIORITY_LOW_POWER
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        // setting LocationRequest
        // on FusedLocationClient
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission_group.LOCATION), PERMISSIONS_ALLOW_USING_LOCATION_ID
            )
        } else {
            locationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper())
        }
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation

            updateViewsWithLocation(lastLocation.latitude, lastLocation.longitude)
        }
    }
    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        /* ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        */
    }
    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_GET_LAST_LOCATION_ID)
    }
    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
    private fun updateViewsWithLocation(latitude: Double, longitude: Double) {
        println(latitude)
        println(longitude)

        binding.latitudeTextView.text = applicationContext.getString(
            R.string.current_latitude, latitude
        )
        binding.longitudeTextView.text = applicationContext.getString(
            R.string.current_longitude, longitude
        )

        mapFragment.getMapAsync {googleMap ->
            /*
            // Add a marker in Sydney, Australia,
            // and move the map's camera to the same location.
            val currentLocation = LatLng(latitude, longitude)

            /*googleMap.addMarker(
                MarkerOptions().position(currentLocation)
                    .title("New Marker")
            )*/

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

            // Zoom in, animating the camera.
            //googleMap.animateCamera(CameraUpdateFactory.zoomIn())

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 3000, null)
            */


            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 3000, null)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude, longitude), 10f))

            //googleMap.uiSettings.isMapToolbarEnabled = true
        }
    }
}