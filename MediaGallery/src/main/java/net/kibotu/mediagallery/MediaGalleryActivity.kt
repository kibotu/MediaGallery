package net.kibotu.mediagallery

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_media_gallery.*
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.internal.presenter.ImagePresenter
import net.kibotu.mediagallery.internal.log
import net.kibotu.mediagallery.internal.onClick
import net.kibotu.mediagallery.internal.transformer.ZoomOutSlideTransformer
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger


class MediaGalleryActivity : AppCompatActivity() {

    private var requests: List<Target<Drawable>>? = null

    private var isBlurrable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_media_gallery)

        val params: Builder.Options? = intent?.extras?.getParcelable(Builder.Options::class.java.canonicalName)

        log { "params=$params" }

        isBlurrable = params?.isBlurrable ?: true

        val adapter = PresenterAdapter()
        val imagePresenter = ImagePresenter(
            isZoomable = params?.isZoomable == true,
            isTranslatable = params?.isTranslatable == true,
            onResourceReady = this::onUpdateBackground
        )
        adapter.registerPresenter(imagePresenter)

        pager.setPageTransformer(ZoomOutSlideTransformer())
        pager.adapter = adapter

        val items = (params?.media ?: emptyList()).map { PresenterModel(it, R.layout.media_gallery_item_presenter) }
        preload(params?.preload, items, imagePresenter.requestOptions)

        adapter.submitList(items)

        quit.onClick { finish() }
    }

    private val factory: DrawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    private fun onUpdateBackground(bitmap: Bitmap?) {
        if (!isBlurrable) return

        blurryBackground.crossfade(bitmap!!)

//        blurryBackground.blurWith(bitmap ?: return) {
//
//            Glide.with(blurryBackground)
//                .load(it)
//                .skipMemoryCache(true)
////                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .transition(withCrossFade(factory))
//                .into(blurryBackground)
//        }
    }

    private fun preload(amount: Int?, items: List<PresenterModel<MediaData>>, requestOptions: RequestOptions) {
        if (amount == null)
            return

        val counter = AtomicInteger(0)

        requests = items.take(amount).map {

            Glide.with(applicationContext!!)
                .applyDefaultRequestOptions(requestOptions.priority(Priority.NORMAL))
                .load(it.model.uri)
                .addListener(GlidePreloadListener(counter, items.size))
                .preload()
        }
    }

    override fun onDestroy() {
        requests?.forEach { it.request?.clear() }
        super.onDestroy()
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
            var preload: Int? = null
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

    /**
     * https://developer.android.com/training/system-ui/immersive
     */
    private fun hideSystemUI() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        if (SDK_INT < KITKAT) return
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    // endregion
}