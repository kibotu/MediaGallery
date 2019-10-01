package net.kibotu.mediagallery.demo.debug

import android.app.Application
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader


/**
 * class to allow code execution only during debug configuration
 */
object BuildConfigRuntime : IBuildConfigRuntime {

    override fun initFlipper(context: Application) {
        SoLoader.init(context, false)

        if (!FlipperUtils.shouldEnableFlipper(context))
            return

        with(AndroidFlipperClient.getInstance(context)) {
            addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            start()
        }
    }
}