package net.kibotu.mediagallery.internal

import android.content.res.Resources
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewTreeObserver
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions


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

internal val requestOptions by lazy {
    RequestOptions
        .fitCenterTransform()
        .priority(Priority.IMMEDIATE)
        .dontAnimate()
        .override(1024)
        .downsample(DownsampleStrategy.CENTER_INSIDE)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
}