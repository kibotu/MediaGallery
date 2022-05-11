package net.kibotu.mediagallery.internal.gestures

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
internal class SwipeDistanceView : View {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    protected val gestureDetector: GestureDetector

    val scrollListener: ScrollListener

    protected var onScroll: ((percentX: Float, percentY: Float) -> Unit)? = null

    protected var onIsScrollingChanged: ((isScrolling: Boolean) -> Unit)? = null

    var requestDisallowInterceptTouchEvent = false

    var isScrolling = false

    init {
        scrollListener = ScrollListener({ measuredWidth }, { measuredHeight }) { percentX, percentY ->
            onScroll?.invoke(percentX, percentY)
        }
        gestureDetector = GestureDetector(context, scrollListener)
    }

    /**
     * @param percentX [-1,1]
     * @param percentY [-1,1]
     */
    fun onScroll(onScroll: ((percentX: Float, percentY: Float) -> Unit)? = null) {
        this.onScroll = onScroll
    }

    /**
     * Notifies when scrolling starts or ends.
     *
     * @param isScrolling true if user is scrolling
     */
    fun onIsScrollingChanged(onIsScrollingChanged: ((isScrolling: Boolean) -> Unit)? = null) {
        this.onIsScrollingChanged = onIsScrollingChanged
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(requestDisallowInterceptTouchEvent)
        return when {
            event?.actionMasked == MotionEvent.ACTION_DOWN && event.pointerCount <= 1 -> {
                gestureDetector.onTouchEvent(event)
            }
            event?.actionMasked == MotionEvent.ACTION_MOVE && event.pointerCount <= 1 -> {
                startScrolling()
                gestureDetector.onTouchEvent(event)
            }
            else -> {
                stopScrolling()
                super.onTouchEvent(event)
            }
        }
    }

    protected fun startScrolling() {
        if (isScrolling)
            return

        isScrolling = true

        onIsScrollingChanged?.invoke(isScrolling)
    }

    protected fun stopScrolling() {
        if (!isScrolling)
            return

        isScrolling = false

        onIsScrollingChanged?.invoke(isScrolling)
    }
}