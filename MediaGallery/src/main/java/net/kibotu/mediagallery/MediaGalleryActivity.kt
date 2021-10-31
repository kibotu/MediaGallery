package net.kibotu.mediagallery

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.parcelize.Parcelize
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import net.kibotu.android.recyclerviewpresenter.PresenterViewModel
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.MediaData
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.databinding.ActivityMediaGalleryBinding
import net.kibotu.mediagallery.internal.hideSystemUI
import net.kibotu.mediagallery.internal.log
import net.kibotu.mediagallery.internal.onClick
import net.kibotu.mediagallery.internal.presenter.ImagePresenter
import net.kibotu.mediagallery.internal.presenter.VideoPresenter
import net.kibotu.mediagallery.internal.requestOptions
import net.kibotu.mediagallery.internal.transformer.ZoomOutSlideTransformer
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger


class MediaGalleryActivity : AppCompatActivity() {

    private var requests: List<Target<Drawable>>? = null

    private var isBlurrable: Boolean = true

    private val binding by lazy { ActivityMediaGalleryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val params: Builder.Options? = intent?.extras?.getParcelable(Builder.Options::class.java.canonicalName)

        if (params == null) {
            Log.w("MediaGalleryActivity", "no params set, please use MediaGalleryActivity.Builder#with")
            finish()
            return
        }

        log("params=$params" )

        binding.init(params)
    }

    private fun ActivityMediaGalleryBinding.init(params: Builder.Options) {
        binding.root.keepScreenOn = params.keepOnScreen

        isBlurrable = params.isBlurrable

        val adapter = PresenterAdapter()

        val imagePresenter = ImagePresenter(
            isZoomable = params.isZoomable,
            isTranslatable = params.isTranslatable,
            onResourceReady = { onUpdateBackground(it ?: return@ImagePresenter) }
        )
        adapter.registerPresenter(imagePresenter)
        adapter.registerPresenter(
            VideoPresenter(
                showVideoControls = params.showVideoControls,
                showVideoControlsTimeOut = params.showVideoControlsTimeOut,
                autoPlay = params.autoPlay
            )
        )

        if (params.showPageIndicator && params.media.size > 1)
            indicator.attachToViewPager(pager)

        pager.setPageTransformer(ZoomOutSlideTransformer())
        pager.adapter = adapter

        val items: MutableList<PresenterViewModel<*>> = (params.media).map {
            PresenterViewModel(
                it, when (it) {
                    is Video -> R.layout.media_gallery_video_presenter
                    is Image -> R.layout.media_gallery_image_presenter
                    else -> R.layout.media_gallery_image_presenter
                }
            )
        }.toMutableList()

        preload(params.preload, params.media.filterIsInstance(Image::class.java), requestOptions)

        adapter.submitList(items)

        pager.setCurrentItem(params.scrollPosition, params.smoothScroll)

        quit.onClick { finish() }
    }

    private fun ActivityMediaGalleryBinding.onUpdateBackground(bitmap: Bitmap) {
        if (!isBlurrable) return

        blurryBackground.crossfade(bitmap)
    }

    override fun onDestroy() {
        requests?.forEach { it.request?.clear() }
        with(binding.pager.adapter as PresenterAdapter) {
            clear()
            unregisterPresenter()
        }
        binding.pager.adapter = null
        super.onDestroy()
    }

    // region glide pre-loading

    private fun preload(amount: Int?, items: List<Image>?, requestOptions: RequestOptions) {
        if (amount == null)
            return

        val counter = AtomicInteger(0)

        requests = items?.take(amount)?.map {

            Glide.with(applicationContext!!)
                .applyDefaultRequestOptions(requestOptions.priority(Priority.NORMAL))
                .load(it.uri)
                .addListener(GlidePreloadListener(counter, items.size))
                .preload()
        }
    }

    private class GlidePreloadListener(val counter: AtomicInteger, val total: Int) : RequestListener<Drawable> {

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            log("onLoadFailed count=${counter.incrementAndGet()} size=${total} $model" )
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            log( "onResourceReady count=${counter.incrementAndGet()} size=${total} $model" )
            return false
        }
    }

    // endregion

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
            var isBlurrable: Boolean = true,
            var preload: Int? = null,
            var showVideoControls: Boolean = false,
            /**
             * Video controls show time int in millis.
             */
            var showVideoControlsTimeOut: Int = 1750,
            var keepOnScreen: Boolean = true,
            var scrollPosition: Int = 0,
            var smoothScroll: Boolean = false,
            var showPageIndicator: Boolean = true
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

    // region full screen

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log( "onConfigurationChanged $newConfig orientation=${newConfig.orientation}" )
    }

    // endregion
}

