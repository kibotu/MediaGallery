package net.kibotu.mediagallery.demo

import android.content.res.Configuration
import androidx.multidex.MultiDexApplication
import net.kibotu.mediagallery.demo.debug.BuildConfigRuntime
import timber.log.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        BuildConfigRuntime.initFlipper(this)
        Timber.plant(Timber.DebugTree())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Timber.i("onConfigurationChanged $newConfig")
    }
}