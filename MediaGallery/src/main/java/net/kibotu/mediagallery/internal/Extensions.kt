package net.kibotu.mediagallery.internal

import android.content.res.Resources
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewTreeObserver


internal fun View.waitForLayout(block: (() -> Unit)?) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            else {
                @Suppress("DEPRECATION")
                viewTreeObserver.removeGlobalOnLayoutListener(this)
            }

            block?.invoke()
        }
    })
}

internal val screenWidthPixels: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

internal val screenHeightPixels: Int
    get() = Resources.getSystem().displayMetrics.heightPixels

internal fun View.onClick(function: () -> Unit) {
    setOnClickListener {
        it.isHapticFeedbackEnabled = true
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        function()
    }
}