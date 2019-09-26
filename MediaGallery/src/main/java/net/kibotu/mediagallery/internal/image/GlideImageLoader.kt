package net.kibotu.mediagallery.internal.image

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory.Builder
import com.bumptech.glide.request.transition.Transition
import com.daimajia.numberprogressbar.NumberProgressBar
import net.kibotu.mediagallery.internal.log

internal class GlideImageLoader(private val backgroundImageView: ImageView, private val imageView: ImageView, private val progressBar: NumberProgressBar? = null) {

    private val crossFade: BitmapTransitionOptions? =
        BitmapTransitionOptions.withCrossFade(Builder().setCrossFadeEnabled(true).build())

    fun load(url: String?, options: RequestOptions?, isBlurrable: Boolean) {
        if (url == null || options == null) return

        onConnecting()

        //set Listener & start

        log { "load url=$url" }

//        ProgressAppGlideModule.expect(url, object : ProgressAppGlideModule.UIonProgressListener {
//
//            override val granualityPercentage: Float = 0.1f
//
//            override fun onProgress(bytesRead: Long, expectedLength: Long) {
//
//                val progress = (100f * bytesRead / expectedLength).toInt()
//                log { "onProgress bytesRead=$bytesRead expectedLength=$expectedLength => progress=$progress" }
//
//                progressBar?.progress = progress
//            }
//        })

        Glide.with(imageView.context.applicationContext)
            .asBitmap()
            .load(url)
            .transition(crossFade!!)
            .apply(options.skipMemoryCache(true))
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
//                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                    ProgressAppGlideModule.forget(url)
                    onFinished()
                    return false
                }
            })
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    if (isBlurrable) {
//                        backgroundImageView.setImageBitmap(resource)
                    }
                    imageView.setImageBitmap(resource)
                }
            })
    }

    private fun onConnecting() {
        progressBar?.isVisible = true
    }

    private fun onFinished() {
        if (progressBar != null) {
            progressBar.isGone = true
            imageView.isVisible = true
        }
    }
}