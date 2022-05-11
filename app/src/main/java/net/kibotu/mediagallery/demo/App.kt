package net.kibotu.mediagallery.demo

import android.app.Application
import android.content.res.Configuration
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.i("onConfigurationChanged $newConfig")
    }
}