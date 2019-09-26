package net.kibotu.mediagallery.internal.image

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

internal class ZoomageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : com.jsibbold.zoomage.ZoomageView(context, attrs, defStyleAttr) {

    internal var view: View? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean = when {
        event?.pointerCount ?: 0 <= 1 -> {
            view?.onTouchEvent(event)
            isTranslatable = false // disable translation while we have only one finger on screen
            super.onTouchEvent(event)
        }
        else -> {
            isTranslatable = true
            super.onTouchEvent(event)
        }
    }
}