package net.kibotu.mediagallery.internal.image

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.media_gallery_item_presenter.view.*
import net.kibotu.android.recyclerviewpresenter.Adapter
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.MediaData
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.internal.log

internal class ImagePresenter(
    val isBlurrable: Boolean = true,
    val isTranslatable: Boolean = true,
    val isZoomable: Boolean = true
) : Presenter<MediaData>() {

    override val layout = R.layout.media_gallery_item_presenter

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: PresenterModel<MediaData>, position: Int, payloads: MutableList<Any>?, adapter: Adapter) {

        log { "$position ${item.model}" }

        with(viewHolder.itemView) {

            image.isZoomable = isZoomable
            image.isTranslatable = isTranslatable

            image.view = swipe_detector

            Glide.with(context.applicationContext)
                .asBitmap()
                .load(item.model.uri)
                .override(layoutParams.width, Target.SIZE_ORIGINAL)
                .transition(BitmapTransitionOptions.withCrossFade(crossFadeFactory))
                .apply(requestOptions)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        if (isBlurrable) {
                            imageBackground.blur(resource)
                        }
                        image.setImageBitmap(resource)
                    }
                })
        }
    }

    private val crossFadeFactory by lazy { DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build() }

    private val requestOptions by lazy {

        RequestOptions
            .fitCenterTransform()
            .priority(Priority.IMMEDIATE)
            .dontAnimate()
            .override(1024)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }
}