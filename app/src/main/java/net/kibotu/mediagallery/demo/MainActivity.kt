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
        // [x] streaming urls
        // [] click listener
        // [x] zoomable
        // [x] translatable
        // [x] player controls
        // [x] blurry
        // [x] crossfade background
        // [x] quit button
        // [] swipe down to quit
        // [x] preload
        // [] preload progressbar
        // [] viewpager indicators
        // [x] resume seek position

        val youtubeVideoId = "bt3qyCqv-eU"
        val youtube360VideoId = "fRcgZnLvwhE"

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

        val youtubeVideo = Video(uri = youtubeVideoId, type = Video.Type.YOUTUBE)
        val youtube360Video = Video(uri = youtube360VideoId, enable360 = true, type = Video.Type.YOUTUBE)
        val assetVideo = Video(uri = "walkaround_with_additional_iframes.mp4", type = Video.Type.ASSETS)
        val externalStorageVideo = Video(uri = "Download/walkaround.mp4", type = Video.Type.EXTERNAL_STORAGE)
        val internalStorageVideo = Video(uri = "walkaround.mp4", type = Video.Type.INTERNAL_STORAGE)
        val fileVideo = Video(uri = "walkaround.mp4".parseAssetFile(), type = Video.Type.FILE)
        val hlsVideo = Video(uri = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", type = Video.Type.HLS)
        val youtubeHlsVideo = Video(
            uri = "https://r1---sn-ntnxax8xo-cxge.googlevideo.com/videoplayback?expire=1569891479&ei=N1CSXc-pBZLhgQfytpSwCQ&ip=37.49.153.11&id=o-ADXTrAvYMG-bud5-f7DP6wfdwRXaOvFKps9_h3lXNRw9&itag=22&source=youtube&requiressl=yes&mm=31%2C29&mn=sn-ntnxax8xo-cxge%2Csn-4g5e6nl7&ms=au%2Crdu&mv=m&mvi=0&pcm2cms=yes&pl=21&initcwndbps=338750&mime=video%2Fmp4&ratebypass=yes&dur=165.209&lmt=1536648392110161&mt=1569869767&fvip=5&fexp=23842630&c=WEB&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cmime%2Cratebypass%2Cdur%2Clmt&sig=ALgxI2wwRQIhALkWpsfuGDDe-56u5Vjfo3_ibPRnipfTGZJCt0YvGJVxAiA23w21hae9bH4jt_-p64RUZXvLkU0fmbYdPIMTdH3qRw%3D%3D&lsparams=mm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpcm2cms%2Cpl%2Cinitcwndbps&lsig=AHylml4wRgIhANb1lXyatOA5mZM1NRDQtdj3_mkazUDL2ZMLb1xGJ4ToAiEA_m4NChOL9jCU-xGfULuGVehLtQ4eg3uxdWZ-Qqc_kSw%3D",
            type = Video.Type.FILE
        )

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
                    add(youtubeVideo)
                    add(youtube360Video)
                    add(youtubeHlsVideo)
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