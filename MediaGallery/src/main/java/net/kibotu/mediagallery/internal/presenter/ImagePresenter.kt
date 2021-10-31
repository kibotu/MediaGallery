package net.kibotu.mediagallery.internal.presenter

import android.graphics.Bitmap
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.daimajia.numberprogressbar.NumberProgressBar
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterViewModel
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.databinding.MediaGalleryImagePresenterBinding
import net.kibotu.mediagallery.internal.log
import net.kibotu.mediagallery.internal.requestOptions
import net.kibotu.resourceextension.resBoolean


internal class ImagePresenter(
    val isTranslatable: Boolean = true,
    val isZoomable: Boolean = true,
    var onResourceReady: ((Bitmap?) -> Unit)? = null,
) : Presenter<Image, MediaGalleryImagePresenterBinding>(R.layout.media_gallery_image_presenter, MediaGalleryImagePresenterBinding::bind) {

    override fun bindViewHolder(viewBinding: MediaGalleryImagePresenterBinding, viewHolder: RecyclerView.ViewHolder, item: PresenterViewModel<Image>, payloads: MutableList<Any>?): Unit =
        with(viewBinding) {

            log("bindViewHolder ${viewHolder.bindingAdapterPosition} ${item.model}")


            image.isZoomable = isZoomable
            image.isTranslatable = isTranslatable

            val uri = item.model.uri.toString()

            if (R.bool.enable_glide_progress_listener.resBoolean)
                ProgressManager.getInstance().addResponseListener(uri, GlideProgressListener(uri, numberProgressBar))

            numberProgressBar.isVisible = true

            image.setImageBitmap(null)

            Glide.with(viewHolder.itemView.context)
                .asBitmap()
                .load(item.model.uri)
                .apply(requestOptions.priority(Priority.IMMEDIATE))
                .transition(withCrossFade())
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                        numberProgressBar.isGone = true
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        numberProgressBar.isGone = true
                        onResourceReady?.invoke(resource)
                        return false
                    }
                })
                .into(image)
                .waitForLayout()
                .clearOnDetach()
        }
}

internal class GlideProgressListener(
    private var url: String,
    private val numberProgressBar: NumberProgressBar
) : ProgressListener {

    override fun onProgress(progressInfo: ProgressInfo?) {

        numberProgressBar.isGone = progressInfo?.isFinish == true

        log("onProgress ${progressInfo?.percent} $progressInfo $url")

        numberProgressBar.progress = progressInfo?.percent ?: 0
    }

    override fun onError(id: Long, e: Exception?) {
        log("onError id=$id ${e?.message}")
        numberProgressBar.isGone = true
    }
}