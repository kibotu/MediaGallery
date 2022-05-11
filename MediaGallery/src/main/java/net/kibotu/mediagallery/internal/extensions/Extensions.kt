package net.kibotu.mediagallery.internal.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.isGone
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */

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
@Suppress("DEPRECATION")
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

@Suppress("DEPRECATION")
internal fun Activity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

internal fun String.parseAssetFile(): Uri = Uri.parse("file:///android_asset/$this")

internal fun Activity.finishWithResult(resultCode: Int, intent: Intent? = null) {
    setResult(resultCode, intent)
    finish()
}


/**
 * Converts dp to pixel.
 */
val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * Converts dp to pixel.
 */
val Int.dp: Int get() = toFloat().dp.toInt()

var TextView.textOrGone: CharSequence?
    get() = text.toString()
    set(value) {
        isGone = value.isNullOrEmpty()
        text = value ?: ""
    }