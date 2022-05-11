package net.kibotu.mediagallery.internal.delegates

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
class WeakReferenceDelegate<T> {

    private var weakReference: WeakReference<T>? = null

    constructor()

    constructor(value: T) {
        weakReference = WeakReference(value)
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? = weakReference?.get()

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        weakReference = WeakReference(value)
    }
}

inline fun <reified T> weak() = WeakReferenceDelegate<T>()

inline fun <reified T> weak(value: T) = WeakReferenceDelegate(value)