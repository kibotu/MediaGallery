package net.kibotu.mediagallery.internal.image

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
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
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (bitmap?.width ?: 0 >= bitmap?.height ?: 0)
                blurWith(bitmap)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            if (bitmap?.height ?: 0 >= bitmap?.width ?: 0)
                blurWith(bitmap)
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

    private fun ImageView.blurWith(bitmap: Bitmap?, radius: Int = 10, scaleFactor: Float = 8f) {

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
            blurryBitmap = Bitmap.createBitmap(
                (width / scaleFactor).toInt(),
                (height / scaleFactor).toInt(),
                Bitmap.Config.RGB_565
            )

        val canvas = Canvas(blurryBitmap!!)
//        canvas.translate(-left.toFloat() + -measuredWidth / 2f, -top.toFloat() / 2f)
//        canvas.scale(1 / scaleFactor, 1 / scaleFactor)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        blurryBitmap = FastBlur.doBlur(blurryBitmap, radius, true)

        setImageBitmap(blurryBitmap)

        log { "view=[$measuredWidth:$measuredHeight]: bitmap=[${bitmap.width}:${bitmap.height}] overlay=[${blurryBitmap?.width}:${blurryBitmap?.height}] in ${System.currentTimeMillis() - startMs} ms" }
    }
}