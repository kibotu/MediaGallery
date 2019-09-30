package net.kibotu.mediagallery.internal.blur

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.core.view.isInvisible

internal class Crossfader(context: Context?, attrs: AttributeSet?) : ViewSwitcher(context, attrs) {

    init {
        setTransition()
        addImageViews()
    }

    fun crossfade(bitmap: Bitmap) = (nextView as? BlurryImageView)?.let { loadImage(it, bitmap) }

    private fun loadImage(imageView: BlurryImageView, bitmap: Bitmap) {
        imageView.blurWith(bitmap) {
            imageView.setImageBitmap(it)
            showNext()
        }
    }

    private fun setTransition() {
        inAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        outAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
    }

    private fun addImageViews() {
        repeat(2) {
            val blurryImageView = BlurryImageView(context)
            blurryImageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            blurryImageView.adjustViewBounds = true
            blurryImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            addView(blurryImageView)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        child?.isInvisible = childCount != 1
    }
}