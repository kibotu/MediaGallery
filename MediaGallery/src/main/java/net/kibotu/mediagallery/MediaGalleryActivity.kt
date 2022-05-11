package net.kibotu.mediagallery

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.parcelize.Parcelize
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import net.kibotu.android.recyclerviewpresenter.PresenterViewModel
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.Media
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.databinding.MediaGalleryActivityBinding
import net.kibotu.mediagallery.internal.delegates.weak
import net.kibotu.mediagallery.internal.extensions.finishWithResult
import net.kibotu.mediagallery.internal.extensions.hideSystemUI
import net.kibotu.mediagallery.internal.extensions.log
import net.kibotu.mediagallery.internal.gestures.OffsetOnPageScrollListener
import net.kibotu.mediagallery.internal.image.ImageListBinder
import net.kibotu.mediagallery.internal.transformer.ZoomOutSlideTransformer
import net.kibotu.mediagallery.internal.video.VideoListBinder
import java.util.concurrent.atomic.AtomicInteger


class MediaGalleryActivity : AppCompatActivity() {

    private var requests: List<Target<Drawable>>? = null

    private lateinit var binding: MediaGalleryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MediaGalleryActivityBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        val params: Builder.Options? = intent?.extras?.getParcelable(Builder.Options::class.java.canonicalName)

        if (params == null || params.media.isEmpty()) {
            log("no params set, please use MediaGalleryActivity.Builder#with")
            finish()
            return
        }

        binding.init(params)
    }

    private fun MediaGalleryActivityBinding.init(params: Builder.Options) {
        log("params=$params")
        root.keepScreenOn = params.keepOnScreen

        val adapter = PresenterAdapter()

        val imageListBinder = ImageListBinder(isZoomable = params.isZoomable)
        adapter.registerPresenter(imageListBinder)
        adapter.registerPresenter(
            VideoListBinder(
                showVideoControls = params.showVideoControls,
                showVideoControlsTimeOut = params.showVideoControlsTimeOut,
                autoPlay = params.autoPlay
            )
        )

        if (params.showPageIndicator && params.media.size > 1)
            indicator.attachToViewPager(pager)

        pager.setPageTransformer(ZoomOutSlideTransformer())

        if (params.isBlurrable) {
            pager.registerOnPageChangeCallback(OffsetOnPageScrollListener(leftImage, rightImage) { params.media })
        }

        pager.adapter = adapter

        val items: MutableList<PresenterViewModel<*>> = (params.media).mapNotNull {
            PresenterViewModel(
                model = it,
                layout = when (it) {
                    is Video -> R.layout.media_gallery_video_presenter
                    is Image -> R.layout.media_gallery_image_presenter
                    else -> return@mapNotNull null
                },
                uuid = it.hashCode().toString()
            )
        }.toMutableList()

        preload(params.preload, params.media.filterIsInstance(Image::class.java), galleryRequestOptions)

        adapter.submitList(items)

        pager.setCurrentItem(params.scrollPosition, params.smoothScroll)

        quit.setOnClickListener {
            finishWithResult(REQUEST_CODE, Intent().apply { putExtra(POSITION, pager.currentItem) })
        }
    }

    override fun onBackPressed() {
        finishWithResult(REQUEST_CODE, Intent().apply { putExtra(POSITION, binding.pager.currentItem) })
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

            Glide.with(applicationContext)
                .applyDefaultRequestOptions(requestOptions.priority(Priority.NORMAL))
                .load(it.uri)
                .addListener(GlidePreloadListener(counter, items.size))
                .preload()
        }
    }

    private class GlidePreloadListener(val counter: AtomicInteger, val total: Int) : RequestListener<Drawable> {

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            log("onLoadFailed count=${counter.incrementAndGet()} size=${total} $model")
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            log("onResourceReady count=${counter.incrementAndGet()} size=${total} $model")
            return false
        }
    }

    // endregion

    // region builder

    class Builder private constructor() {

        private var context by weak<Context?>()

        private var options: Options = Options()

        @Parcelize
        data class Options(
            var media: List<Media> = emptyList(),
            var autoPlay: Boolean = false,
            var isZoomable: Boolean = true,
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

        fun startActivity() = context?.startActivity(with(Intent(context, MediaGalleryActivity::class.java)) { putExtra(Options::class.java.canonicalName, options) })

        fun startActivityForResult(contract: ActivityResultLauncher<Intent>) =
            contract.launch(with(Intent(context, MediaGalleryActivity::class.java)) { putExtra(Options::class.java.canonicalName, options) })

        companion object {

            fun with(context: Context, build: Options.() -> Unit): Builder = Builder().apply {
                this.context = context
                options.build()
            }

            val debug = BuildConfig.DEBUG
        }
    }

    // endregion

    // region full screen

    // region full screen

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged $newConfig orientation=${newConfig.orientation}")
    }

    // endregion

    companion object {
        const val REQUEST_CODE = 10001
        const val POSITION = "POSITION"
    }
}

internal val galleryRequestOptions by lazy {
    RequestOptions
        .fitCenterTransform()
        .priority(Priority.IMMEDIATE)
        .dontAnimate()
        .override(1024)
        .downsample(DownsampleStrategy.CENTER_INSIDE)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
}
