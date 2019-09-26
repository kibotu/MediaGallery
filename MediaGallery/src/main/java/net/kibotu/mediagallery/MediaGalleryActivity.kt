package net.kibotu.mediagallery

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_media_gallery.*
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.internal.image.ImagePresenter
import net.kibotu.mediagallery.internal.log
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

class MediaGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_gallery)

        val params: Builder.Options? = intent?.extras?.getParcelable(Builder.Options::class.java.canonicalName)

        log { "params=$params" }

        val adapter = PresenterAdapter()
        val imagePresenter = ImagePresenter(
            isBlurrable = params?.isBlurrable == true,
            isZoomable = params?.isZoomable == true,
            isTranslatable = params?.isTranslatable == true
        )
        adapter.registerPresenter(imagePresenter)
        pager.adapter = adapter

        val items = (params?.media ?: emptyList()).map { PresenterModel(it, R.layout.media_gallery_item_presenter) }

        if (params?.preload == true)
            preload(items, imagePresenter.requestOptions)

        adapter.submitList(items)
    }

    private fun preload(items: List<PresenterModel<MediaData>>, requestOptions: RequestOptions) {

        val counter = AtomicInteger(0)

        items.forEach {

            Glide.with(applicationContext!!)
                .applyDefaultRequestOptions(requestOptions.priority(Priority.NORMAL))
                .load(it.model.uri)
                .addListener(GlidePreloadListener(counter, items.size))
                .preload()
        }
    }

    private class GlidePreloadListener(val counter: AtomicInteger, val total: Int) : RequestListener<Drawable> {

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            log { "onLoadFailed count=${counter.incrementAndGet()} size=${total} $model" }
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            log { "onResourceReady count=${counter.incrementAndGet()} size=${total} $model" }
            return false
        }
    }

    // region builder

    class Builder private constructor() {

        private lateinit var context: WeakReference<Context>

        private var options: Options = Options()

        @Parcelize
        data class Options(
            var media: List<MediaData> = emptyList(),
            var autoPlay: Boolean = false,
            var isZoomable: Boolean = true,
            var isTranslatable: Boolean = true,
            var showVideoControls: Boolean = false,
            var isBlurrable: Boolean = true,
            var preload: Boolean = true
        ) : Parcelable

        fun startActivity() = context.get()!!.startActivity(with(Intent(context.get(), MediaGalleryActivity::class.java)) { putExtra(Options::class.java.canonicalName, options) })

        companion object {

            fun with(context: Context, build: Options.() -> Unit): Builder = Builder().apply {
                this.context = WeakReference(context)
                options.build()
            }
        }
    }

    // endregion
}