/**
 * Created by [Jan Rabe](https://kibotu.net).
 */

@file:JvmName("DebugExtensions")

package net.kibotu.mediagallery.internal.extensions

import android.util.Log
import net.kibotu.mediagallery.BuildConfig
import net.kibotu.mediagallery.MediaGalleryActivity.Builder.Companion.debug


internal fun Any.log(message: String?) {
    if (!debug || message?.isNotEmpty() != true) return
    Log.d(this::class.java.simpleName, message)
}
