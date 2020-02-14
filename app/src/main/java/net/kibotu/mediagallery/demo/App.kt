package net.kibotu.mediagallery.demo

import android.content.res.Configuration
import android.util.Log
import androidx.multidex.MultiDexApplication
import net.kibotu.logger.TAG
import net.kibotu.mediagallery.demo.debug.BuildConfigRuntime

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        BuildConfigRuntime.initFlipper(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.i(TAG, "onConfigurationChanged $newConfig")
    }
}