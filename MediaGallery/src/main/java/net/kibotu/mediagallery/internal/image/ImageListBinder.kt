package net.kibotu.mediagallery.internal.image

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterViewModel
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.databinding.MediaGalleryImagePresenterBinding
import net.kibotu.mediagallery.galleryRequestOptions
import net.kibotu.mediagallery.internal.extensions.log

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
internal class ImageListBinder(
    val isZoomable: Boolean = true
) : Presenter<Image, MediaGalleryImagePresenterBinding>(R.layout.media_gallery_image_presenter, MediaGalleryImagePresenterBinding::bind) {

    override fun bindViewHolder(viewBinding: MediaGalleryImagePresenterBinding, viewHolder: RecyclerView.ViewHolder, item: PresenterViewModel<Image>, payloads: MutableList<Any>?) {
        with(viewBinding) {
            image.isZoomable = isZoomable
            image.setImageBitmap(null)
            progressBar.isVisible = true
            image.loadAsBitmap(item.model.uri) {
                progressBar.isGone = true
            }
        }
    }

    private fun ImageView.loadAsBitmap(uri: Uri, onComplete: () -> Unit) = Glide.with(context)
        .asBitmap()
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .apply(galleryRequestOptions.priority(Priority.IMMEDIATE))
        .load(uri)
        .transition(BitmapTransitionOptions.withCrossFade())
        .listener(object : RequestListener<Bitmap?> {

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                onComplete()
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                onComplete()
                return false
            }
        })
        .into(this)

}