package com.madrat.myapp

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import com.madrat.myapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(R.layout.activity_maps) {
    companion object {
        const val TAG = "MapsActivity"
    }
    // ViewBinding
    private lateinit var binding: ActivityMapsBinding
    
    // SupportMapFragment
    private var mapFragment: SupportMapFragment? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize dependencies and listeners
        initializeDependencies()
        initializeListeners()
    
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
            locationPermissionGranted(this)
        }
    }
    
    // Initialization
    private fun initializeDependencies() {
        initializeViewBinding()
        initializeSupportMapFragment()
    }
    private fun initializeViewBinding() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
    private fun initializeSupportMapFragment() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
    }
    private fun initializeListeners() {
    
    }
    
    @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun locationPermissionGranted(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        
        //create location request
        val locationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 100L
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        
        //create callback
        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                useLocation(locationResult?.lastLocation)
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
            
            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                if (locationAvailability?.isLocationAvailable == false) {
                    Toast.makeText(context, "Не удалось получить локацию", Toast.LENGTH_SHORT).show()
                    fusedLocationProviderClient.removeLocationUpdates(this)
                } else {
                    super.onLocationAvailability(locationAvailability)
                }
            }
        }
        
        val currentLocationTask = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
        
        currentLocationTask.addOnCompleteListener {task->
            useLocation(task.result)
        }
        
        //request location update
        /*fusedLocationProviderClient
            .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
            .let { task ->
                task.addOnCanceledListener {
                    Toast.makeText(context, "Не удалось получить локацию", Toast.LENGTH_SHORT).show()
                }
                task.addOnFailureListener { exception ->
                    Toast.makeText(context, "Не удалось получить локацию", Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
                task.addOnSuccessListener {}
            }*/
    }
    fun useLocation(lastLocation: Location?) {
        if (lastLocation == null) {
            Log.d(TAG, "location is null")
        } else {
            Log.d(TAG, "My location is: ${lastLocation.latitude}, " +
                "${lastLocation.longitude}")
        }
    }
}