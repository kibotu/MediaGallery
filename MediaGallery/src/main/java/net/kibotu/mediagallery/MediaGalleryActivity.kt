package net.kibotu.mediagallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_media_gallery.*
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.internal.image.ImagePresenter
import net.kibotu.mediagallery.internal.log
import java.lang.ref.WeakReference

class MediaGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_gallery)

        val params: Builder.Options? = intent?.extras?.getParcelable(Builder.Options::class.java.canonicalName)

        log { "params=$params" }

        val adapter = PresenterAdapter()
        adapter.registerPresenter(
            ImagePresenter(
                isBlurrable = params?.isBlurrable == true,
                isZoomable = params?.isZoomable == true,
                isTranslatable = params?.isTranslatable == true
            )
        )
        pager.adapter = adapter

        val items = (params?.media ?: emptyList()).map { PresenterModel(it, R.layout.media_gallery_item_presenter) }

        adapter.submitList(items)
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
            var isBlurrable: Boolean = true
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