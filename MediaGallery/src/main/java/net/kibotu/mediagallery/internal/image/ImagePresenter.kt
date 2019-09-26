package net.kibotu.mediagallery.internal.image

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.media_gallery_item_presenter.view.*
import net.kibotu.android.recyclerviewpresenter.Adapter
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.MediaData
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.internal.log
import net.kibotu.mediagallery.internal.waitForLayout

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

//            imageBackground.waitForLayout {
                GlideImageLoader(imageBackground, image, number_progress_bar)
                    .load(item.model.uri.toString(), requestOptions, isBlurrable)
//            }
        }
    }

    private val requestOptions by lazy {

        RequestOptions
            .fitCenterTransform()
            .priority(Priority.IMMEDIATE)
//            .dontAnimate()
//            .override(1024)
//            .downsample(DownsampleStrategy.CENTER_INSIDE)
//            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }
}