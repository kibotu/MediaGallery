package net.kibotu.mediagallery.internal.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import net.kibotu.mediagallery.internal.log

internal class BlurryImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint by lazy { Paint().apply { flags = Paint.FILTER_BITMAP_FLAG } }

    private var blurryBitmap: Bitmap? = null

    fun blur(bitmap: Bitmap?) {
        blurWith(bitmap) {
            setImageBitmap(bitmap)
        }
    }

    /**
     * Converts dp to pixel.
     */
    private val Float.dp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context!!.resources.displayMetrics
        )

    fun blurWith(bitmap: Bitmap?, radius: Int = 10, scaleFactor: Float = 8f, onBlurredBitmap: (Bitmap) -> Unit) {

        if (bitmap == null)
            return

        var width = measuredWidth
        var height = measuredHeight

        if (width <= 0 || height <= 0) {
            width = context?.resources?.configuration?.screenWidthDp?.toFloat()?.dp?.toInt() ?: 0
            height = context?.resources?.configuration?.screenHeightDp?.toFloat()?.dp?.toInt() ?: 0
        }

        if (width <= 0 || height <= 0) {
            return
        }

        val startMs = System.currentTimeMillis()

        if (blurryBitmap == null)
            blurryBitmap = Bitmap.createBitmap((width / scaleFactor).toInt(), (height / scaleFactor).toInt(), Bitmap.Config.RGB_565)

        val canvas = Canvas(blurryBitmap!!)
        // todo move blur background to center, possible scale back
//         canvas.translate(-left.toFloat() + -measuredWidth / 2f, -top.toFloat() / 2f)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        blurryBitmap = FastBlur.doBlur(blurryBitmap, radius, true)

        onBlurredBitmap.invoke(blurryBitmap!!)

        log { "view=[$measuredWidth:$measuredHeight]: bitmap=[${bitmap.width}:${bitmap.height}] blurryBitmap=[${blurryBitmap?.width}:${blurryBitmap?.height}] canvas=${canvas.width}x${canvas.height} in ${System.currentTimeMillis() - startMs} ms sx=${(width / scaleFactor).toInt()} sy=${(width / scaleFactor).toInt()}" }
    }
}
