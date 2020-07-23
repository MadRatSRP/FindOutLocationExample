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
        private const val PERMISSION_ID = 44

        private const val PERMISSIONS_LOCATION_ID = 100
    }

    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationClient: FusedLocationProviderClient

    // Get the SupportMapFragment and request notification
    // when the map is ready to be used.
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding initialization
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                        this, arrayOf(Manifest.permission_group.LOCATION), PERMISSIONS_LOCATION_ID
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
        val mLocationRequest = LocationRequest()

        // For a high level accuracy use PRIORITY_HIGH_ACCURACY argument.
        // For a low level accuracy (city), use PRIORITY_LOW_POWER
        mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission_group.LOCATION), PERMISSIONS_LOCATION_ID
            )
        } else {
            locationClient.requestLocationUpdates(mLocationRequest,
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
            Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }
    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
    private fun updateViewsWithLocation(latitude: Double, longitude: Double) {
        binding.latitudeTextView.text = applicationContext.getString(
            R.string.current_latitude, latitude
        )
        binding.longitudeTextView.text = applicationContext.getString(
            R.string.current_longitude, longitude
        )

        mapFragment.getMapAsync {googleMap ->
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
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
        }
    }
}