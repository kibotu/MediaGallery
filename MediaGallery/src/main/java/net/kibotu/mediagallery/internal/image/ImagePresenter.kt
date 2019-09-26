package net.kibotu.mediagallery.internal.image

import android.graphics.Bitmap
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.daimajia.numberprogressbar.NumberProgressBar
import kotlinx.android.synthetic.main.media_gallery_item_presenter.view.*
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
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

//            image.view = swipe_detector

            val uri = item.model.uri.toString()

            ProgressManager.getInstance().addResponseListener(uri, GlideProgressListener(uri, number_progress_bar))

            number_progress_bar.isVisible = true

            image.setImageBitmap(null)
            imageBackground.setImageBitmap(null)

            Glide.with(context.applicationContext)
                .asBitmap()
                .load(item.model.uri)
                .apply(requestOptions.priority(Priority.IMMEDIATE))
                .transition(crossFade!!)
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                        number_progress_bar.isGone = true
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        number_progress_bar.isGone = true
                        if (isBlurrable) {
                            imageBackground.blur(resource)
                        }
                        return false
                    }
                })
                .into(image)
                .waitForLayout()
                .clearOnDetach()
        }
    }

     val requestOptions by lazy {

        RequestOptions
            .fitCenterTransform()
            .priority(Priority.IMMEDIATE)
            .dontAnimate()
            .override(1024)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    private val crossFade: BitmapTransitionOptions? =
        BitmapTransitionOptions.withCrossFade(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build())
}

internal class GlideProgressListener(
    private var url: String,
    private val numberProgressBar: NumberProgressBar
) : ProgressListener {

    override fun onProgress(progressInfo: ProgressInfo?) {

        numberProgressBar.isGone = progressInfo?.isFinish == true

        log { "onProgress ${progressInfo?.percent} $progressInfo $url" }

        numberProgressBar.progress = progressInfo?.percent ?: 0
    }

    override fun onError(id: Long, e: Exception?) {
        log { "onError id=$id ${e?.message}" }
        numberProgressBar.isGone = true
    }
}