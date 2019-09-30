package net.kibotu.mediagallery.demo

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exozet.android.core.base.CompositeDisposableHolder
import com.exozet.android.core.extensions.fileExists
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.extensions.parseAssetFile
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.logger.Logger.logv
import net.kibotu.logger.Logger.logw
import net.kibotu.mediagallery.MediaGalleryActivity
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.MediaData
import net.kibotu.mediagallery.data.Video
import net.kibotu.resourceextension.screenHeightPixels
import net.kibotu.resourceextension.screenWidthPixels


class MainActivity : AppCompatActivity(), CompositeDisposableHolder {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogger(LogcatLogger())
        RxPermissions(this)
            .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe({
                if (it.granted)
                    init()
            }, {
                logw { "permission $it" }
            }).addTo(subscription)
    }

    private fun init() {
        logv { "window=${screenWidthPixels}x$screenHeightPixels " }

        // [x] list of imageMedia objects
        // [] list of video media objects
        // [x] asset uris
        // [x] hls uris
        // [x] file uri
        // [] youtube
        // [] 360
        // [] youtube 360
        // [x] images
        // [] streaming urls
        // [] click listener
        // [x] zoomable
        // [x] translatable
        // [x] player controls
        // [x] blurry
        // [x] crossfade background
        // [x] quit button
        // [] youtube videos
        // [] swipe down to quit
        // [x] preload
        // [] preload progressbar
        // [] viewpager indicators
        // [x] resume seek position

        val youtubeVideo = "Q-oluoEQCk0"
        val youtube360Video = "fRcgZnLvwhE"

        // server supports content-length header (required for progress)
        val list = listOf(
            "https://api1.europapark.de/detail-5.7/haupteingang.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_1.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_2.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_3.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_4.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_5.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_6.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_7.jpg",
            "https://api1.europapark.de/detail-5.7/silverstar_8.jpg"
        ).map { Uri.parse(it) }

        val assetVideo = Video(uri = "walkaround_with_additional_iframes.mp4", type = Video.Type.ASSETS)
        val externalStorageVideo = Video(uri = "Download/walkaround.mp4", type = Video.Type.EXTERNAL_STORAGE)
        val internalStorageVideo = Video(uri = "walkaround.mp4", type = Video.Type.INTERNAL_STORAGE)
        val fileVideo = Video(uri = "walkaround.mp4".parseAssetFile(), type = Video.Type.FILE)
        val hlsVideo = Video(uri = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", type = Video.Type.HLS)

        logv { "exists: ${externalStorageVideo.uri} ${externalStorageVideo.uri.fileExists}" }

        image_gallery.onClick {
            var uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { Image(uri = it) }
                preload = media.size
            }.startActivity()
        }

        video_gallery.onClick {
            var uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = mutableListOf<MediaData>().apply {
                    add(assetVideo)
                    add(externalStorageVideo)
                    add(internalStorageVideo)
                    add(fileVideo)
                    add(hlsVideo)
                }
            }.startActivity()
        }

        mixed_gallery.onClick {
            var uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            uris = list
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                autoPlay = true
                media = mutableListOf<MediaData>().apply {
                    add(assetVideo)
                    add(hlsVideo)
                    add(fileVideo)
                    add(externalStorageVideo)
                    addAll(uris.map { Image(uri = it) })

                }
            }.startActivity()
        }

        youtube_videos.onClick {
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = listOf(assetVideo, assetVideo, assetVideo, assetVideo)
                preload = media.size.coerceAtMost(10)
                autoPlay = false
            }.startActivity()
        }

        mixed_gallery.performClick()
    }

    // region CompositeDisposableHolder

    override var subscription = CompositeDisposable()

    override fun disposeCompositeDisposable() {
        if (!subscription.isDisposed)
            subscription.dispose()
    }

    // endregion
}