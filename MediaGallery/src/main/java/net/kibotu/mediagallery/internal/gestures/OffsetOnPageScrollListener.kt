package net.kibotu.mediagallery.internal.gestures

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import net.kibotu.mediagallery.internal.blur.BlurryImageView
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.Media
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.internal.delegates.weak
import net.kibotu.mediagallery.galleryRequestOptions
/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
internal class OffsetOnPageScrollListener(
    private val leftImage: BlurryImageView,
    private val rightImage: BlurryImageView,
    private val getMedia: () -> List<Media>
) : ViewPager2.OnPageChangeCallback() {

    private var lastPosition: Int? = null

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        leftImage.alpha = 1 - positionOffset
        rightImage.alpha = positionOffset

        if (lastPosition == position) return
        lastPosition = position

        val media = getMedia()

        val leftPosition = position.coerceIn(0, media.lastIndex)
        val left = media[leftPosition]
        if (left is Image) leftImage.loadAsBitmap(left.uri)
        if (left is Video) leftImage.setImageBitmap(null)

        val rightPosition = position.plus(1).coerceIn(0, media.lastIndex)
        val right = media[rightPosition]
        if (right is Image) rightImage.loadAsBitmap(right.uri)
        if (right is Video) rightImage.setImageBitmap(null)
    }

    override fun onPageSelected(position: Int) {
        leftImage.alpha = 1f
        rightImage.alpha = 0f

        val media = getMedia()

        val leftPosition = position.coerceIn(0, media.lastIndex)
        val left = media[leftPosition]
        if (left is Image) leftImage.loadAsBitmap(left.uri)
        if (left is Video) leftImage.setImageBitmap(null)
    }

    companion object {

        private fun BlurryImageView.loadAsBitmap(uri: Uri) = Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .dontAnimate()
            .apply(galleryRequestOptions.priority(Priority.IMMEDIATE))
            .load(uri)
            .into(BitmapTarget(this))
    }

    private class BitmapTarget(blurryImageView: BlurryImageView) : CustomTarget<Bitmap>() {

        val view by weak<BlurryImageView>(blurryImageView)

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            view?.setImageBitmap(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) = Unit
    }
}