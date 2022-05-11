package net.kibotu.mediagallery.internal.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2.PageTransformer

/**
 * https://github.com/geftimov/android-viewpager-transformers/wiki/ZoomOutSlideTransformer
 */
abstract class BaseTransformer : PageTransformer {

    /**
     * Called each [.transformPage].
     *
     * @param view
     * @param position
     */
    protected abstract fun onTransform(view: View, position: Float)

    override fun transformPage(view: View, position: Float) {
        onPreTransform(view, position)
        onTransform(view, position)
        onPostTransform(view, position)
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * visibility of the fragment to [android.view.View.GONE]. Returning false will force the fragment to [android.view.View.VISIBLE].
     *
     * @return
     */
    protected fun hideOffscreenPages(): Boolean = true

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected val isPagingEnabled: Boolean
        get() = false

    /**
     * Called each [.transformPage] before {[.onTransform] is called.
     *
     * @param view
     * @param position
     */
    protected fun onPreTransform(view: View, position: Float) {
        val width = view.width.toFloat()
        view.rotationX = 0f
        view.rotationY = 0f
        view.rotation = 0f
        view.scaleX = 1f
        view.scaleY = 1f
        view.pivotX = 0f
        view.pivotY = 0f
        view.translationY = 0f
        view.translationX = if (isPagingEnabled) 0f else -width * position
        if (hideOffscreenPages()) {
            view.alpha = if (position <= -1f || position >= 1f) 0f else 1f
        } else {
            view.alpha = 1f
        }
    }

    /**
     * Called each [.transformPage] call after [.onTransform] is finished.
     *
     * @param view
     * @param position
     */
    protected fun onPostTransform(view: View, position: Float) = Unit
}