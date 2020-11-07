package com.madrat.myapp

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.hoc081098.viewbindingdelegate.viewBinding
import com.madrat.myapp.databinding.ActivityMapsBinding

class MapsActivity: AppCompatActivity(R.layout.activity_maps) {
    // ViewBinding
    private val viewBinding by viewBinding<ActivityMapsBinding>()
    
    // SupportMapFragment
    private lateinit var mapFragment: SupportMapFragment
    
}