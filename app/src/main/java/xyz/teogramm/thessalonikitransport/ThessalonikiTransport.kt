package xyz.teogramm.thessalonikitransport

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp
import xyz.teogramm.thessalonikitransport.service.Notifications

// Class created for Hilt dependency injection. see
// https://developer.android.com/training/dependency-injection/hilt-android#application-class
@HiltAndroidApp
class ThessalonikiTransport : Application(){

    override fun onCreate() {
        super.onCreate()
        // Initialize google maps renderer
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, null);

        // Create notification channels
        Notifications.createNotificationChannels(this.applicationContext)
    }

}
