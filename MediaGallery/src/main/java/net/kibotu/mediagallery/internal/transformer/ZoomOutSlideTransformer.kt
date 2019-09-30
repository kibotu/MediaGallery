package net.kibotu.mediagallery.internal.transformer

import android.view.View
import net.kibotu.mediagallery.R
import kotlin.math.abs
import kotlin.math.max


class ZoomOutSlideTransformer(private val minScale: Float = 0.85f, private val minAlpha: Float = 0.75f, private val enableAlpha: Boolean = false) : BaseTransformer() {

    override fun onTransform(view: View, position: Float) {

        val image = view.findViewById(R.id.image) ?: view

        if (position < -1.0f && position > 1.0f) return

        val height = view.height.toFloat()
        val scaleFactor = max(minScale, 1.0f - abs(position))
        val vertMargin = height * (1.0f - scaleFactor) / 2.0f
        val horzMargin = view.width.toFloat() * (1.0f - scaleFactor) / 2.0f
        view.pivotY = 0.5f * height
        val translateX = if (position < 0.0f) {
            horzMargin - vertMargin / 2.0f
        } else {
            -horzMargin + vertMargin / 2.0f
        }
        view.translationX = translateX
        image.scaleX = scaleFactor
        image.scaleY = scaleFactor
        if (enableAlpha)
            view.alpha = minAlpha + (scaleFactor - minScale) / 0.14999998F * minAlpha
    }
}