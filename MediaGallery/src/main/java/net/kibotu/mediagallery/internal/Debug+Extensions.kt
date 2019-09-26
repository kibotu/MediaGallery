/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */

@file:JvmName("DebugExtensions")

package net.kibotu.mediagallery.internal

import android.util.Log
import net.kibotu.mediagallery.BuildConfig

internal val debug = BuildConfig.DEBUG

internal fun Any.log(block: () -> String?) {
    if (debug)
        Log.d(this::class.java.simpleName, "${block()}")
}

internal fun Exception.log(block: () -> String?) {
    if (debug)
        Log.d(this::class.java.simpleName, "${block()}")
}