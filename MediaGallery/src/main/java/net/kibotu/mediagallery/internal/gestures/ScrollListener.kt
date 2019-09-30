package net.kibotu.mediagallery.internal.gestures

import android.view.GestureDetector
import android.view.MotionEvent

internal open class ScrollListener(val width: () -> Int, val height: () -> Int, val onScroll: (percentX: Float, percentY: Float) -> Unit) : GestureDetector.SimpleOnGestureListener() {

    /**
     * Min Swipe X-Distance
     */
    var thresholdX  = 3f

    /**
     * Min Swipe Y-Distance
     */
    var thresholdY  = 3f

    /**
     * Starting X-Position
     */
    protected var startX = 0f

    /**
     * Starting Y-Position
     */
    protected var startY = 0f

    override fun onDown(e: MotionEvent?): Boolean {
        startX = e?.x ?: 0f
        startY = e?.y ?: 0f
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {

        val dX = startX - (e2?.x ?: 0f)
        val dY = startY - (e2?.y ?: 0f)

        return when {
            Math.abs(dX) <= thresholdX && Math.abs(dY) <= thresholdY -> false
            else -> {

                val percentX = (dX) / width()
                val percentY = (dY) / height()

                onScroll(percentX, percentY)

                true
            }
        }
    }
}