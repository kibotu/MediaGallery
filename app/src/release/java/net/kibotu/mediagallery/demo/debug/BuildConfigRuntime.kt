package net.kibotu.mediagallery.demo.debug

import android.app.Application


/**
 * class to allow code execution only during debug configuration
 */
object BuildConfigRuntime : IBuildConfigRuntime {

    override fun initFlipper(context: Application) {}
}