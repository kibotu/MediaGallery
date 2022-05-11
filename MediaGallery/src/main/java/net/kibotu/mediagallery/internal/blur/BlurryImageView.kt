package net.kibotu.mediagallery.internal.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kibotu.mediagallery.internal.extensions.dp

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */

internal class BlurryImageView : AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint by lazy { Paint().apply { flags = Paint.FILTER_BITMAP_FLAG } }

    private var blurryBitmap: Bitmap? = null

    private var blurJob: Job? = null

    override fun setImageBitmap(bm: Bitmap?) {
        if (bm == null) {
            super.setImageBitmap(null)
            return
        }

        blurJob?.cancel()

        blurJob = findViewTreeLifecycleOwner()
            ?.lifecycleScope
            ?.launch {
                val bitmap = blurWith(bm) ?: return@launch
                super.setImageBitmap(bitmap)
            }
    }

    private suspend fun blurWith(bitmap: Bitmap?, radius: Int = 10, scaleFactor: Float = 8f): Bitmap? = withContext(Dispatchers.Main) {
        if (bitmap == null) return@withContext null

        var width = measuredWidth
        var height = measuredHeight

        if (width <= 0 || height <= 0) {
            width = context?.resources?.configuration?.screenWidthDp?.dp ?: 0
            height = context?.resources?.configuration?.screenHeightDp?.dp ?: 0
        }

        if (width <= 0 || height <= 0) return@withContext null

        if (blurryBitmap == null) {
            blurryBitmap = Bitmap.createBitmap((width / scaleFactor).toInt(), (height / scaleFactor).toInt(), Bitmap.Config.RGB_565)
        }

        val canvas = Canvas(requireNotNull(blurryBitmap))
        // todo move blur background to center, possible scale back
//         canvas.translate(-left.toFloat() + -measuredWidth / 2f, -top.toFloat() / 2f)
        canvas.scale(0.5f, 0.5f)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        blurryBitmap = FastBlur.doBlur(requireNotNull(blurryBitmap), radius, true)

        blurryBitmap
    }
}
