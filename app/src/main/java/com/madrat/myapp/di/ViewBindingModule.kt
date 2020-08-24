package com.madrat.myapp.di

import androidx.fragment.app.FragmentActivity
import com.madrat.myapp.databinding.ActivityMapsBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ViewBindingModule {
    @Provides
    fun provideBinding(activity: FragmentActivity): ActivityMapsBinding {
        return ActivityMapsBinding.inflate(activity.layoutInflater)
    }
}