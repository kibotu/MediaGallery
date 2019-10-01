package net.kibotu.mediagallery.demo

import androidx.multidex.MultiDexApplication
import net.kibotu.mediagallery.demo.debug.BuildConfigRuntime

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        BuildConfigRuntime.initFlipper(this)
    }
}