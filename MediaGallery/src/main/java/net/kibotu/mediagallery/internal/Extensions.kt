package net.kibotu.mediagallery.internal

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File


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

/**
 * https://developer.android.com/training/system-ui/immersive
 */
internal fun Activity.hideSystemUI() {
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

internal fun Activity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

internal fun String.parseAssetFile(): Uri = Uri.parse("file:///android_asset/$this")

internal fun String.parseInternalStorageFile(context: Context): Uri = Uri.parse("${context.applicationContext!!.filesDir.absolutePath}/$this")

internal fun String.parseExternalStorageFile(): Uri = Uri.parse("${Environment.getExternalStorageDirectory()}/$this")

internal fun String.parseFile(): Uri = Uri.fromFile(File(this))

internal val Uri.fileExists: Boolean
    get() = File(toString()).exists()