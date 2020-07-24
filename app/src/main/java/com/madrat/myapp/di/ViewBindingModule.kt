package com.madrat.myapp.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.madrat.myapp.MapsActivity
import com.madrat.myapp.databinding.ActivityMapsBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

object ViewBindingModule {
    fun provideViewBinding(activity: Activity)
            : ActivityMapsBinding {
        return ActivityMapsBinding
            .inflate(activity.layoutInflater)
    }
}