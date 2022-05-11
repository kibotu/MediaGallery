package net.kibotu.mediagallery.demo

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import net.kibotu.mediagallery.MediaGalleryActivity
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.Media
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.demo.databinding.ActivityMainBinding
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        subscription.add(
            RxPermissions(this)
                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({
                    if (it.granted)
                        binding.init()
                }, {
                    Timber.v("permission $it")
                })
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun ActivityMainBinding.init() {
//        v( "window=${screenWidthPixels}x$screenHeightPixels " )

        // [x] list of imageMedia objects
        // [] list of video media objects
        // [x] asset uris
        // [x] hls uris
        // [x] file uri
        // [x] youtube
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

        val youtubeVideoId = "oYC8KkNIkZk"
        val youtube360VideoId = "XNTNS8hlWZ8"

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
        val fileVideo = Video(uri = "walkaround_with_additional_iframes.mp4".parseAssetFile().toString(), type = Video.Type.FILE)
        val hlsVideo = Video(uri = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", type = Video.Type.HLS)
        val hls2Video = Video(
            uri = "https://multiplatform-f.akamaihd.net/i/multi/april11/sintel/sintel-hd_,512x288_450_b,640x360_700_b,768x432_1000_b,1024x576_1400_m,.mp4.csmil/master.m3u8",
            type = Video.Type.HLS
        )
        val youtubeHlsVideo = Video(
            uri = "https://r1---sn-ntnxax8xo-cxge.googlevideo.com/videoplayback?expire=1569891479&ei=N1CSXc-pBZLhgQfytpSwCQ&ip=37.49.153.11&id=o-ADXTrAvYMG-bud5-f7DP6wfdwRXaOvFKps9_h3lXNRw9&itag=22&source=youtube&requiressl=yes&mm=31%2C29&mn=sn-ntnxax8xo-cxge%2Csn-4g5e6nl7&ms=au%2Crdu&mv=m&mvi=0&pcm2cms=yes&pl=21&initcwndbps=338750&mime=video%2Fmp4&ratebypass=yes&dur=165.209&lmt=1536648392110161&mt=1569869767&fvip=5&fexp=23842630&c=WEB&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cmime%2Cratebypass%2Cdur%2Clmt&sig=ALgxI2wwRQIhALkWpsfuGDDe-56u5Vjfo3_ibPRnipfTGZJCt0YvGJVxAiA23w21hae9bH4jt_-p64RUZXvLkU0fmbYdPIMTdH3qRw%3D%3D&lsparams=mm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpcm2cms%2Cpl%2Cinitcwndbps&lsig=AHylml4wRgIhANb1lXyatOA5mZM1NRDQtdj3_mkazUDL2ZMLb1xGJ4ToAiEA_m4NChOL9jCU-xGfULuGVehLtQ4eg3uxdWZ-Qqc_kSw%3D",
            type = Video.Type.FILE
        )

        imageGallery.setOnClickListener {
            val uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this@MainActivity) {
                autoPlay = true
                isBlurrable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { Image(uri = it.toString()) }
                preload = media.size
                scrollPosition = 3
                smoothScroll = false
            }.startActivity()
        }

        videoGallery.setOnClickListener {
            MediaGalleryActivity.Builder.with(this@MainActivity) {
                autoPlay = true
                isBlurrable = true
                isZoomable = true
                showVideoControls = true
                autoPlay = true
                media = buildList<Media> {
                    add(assetVideo)
                    add(hlsVideo)
                    add(youtube360Video)
                    add(youtubeHlsVideo)
                    add(hls2Video)
                    add(fileVideo)
                }
            }.startActivity()
        }

        mixedGallery.setOnClickListener {
            val uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this@MainActivity) {
                autoPlay = true
                isBlurrable = true
                isZoomable = true
                showVideoControls = true
                autoPlay = true
                media = buildList {
                    add(hlsVideo)
                    add(assetVideo)
                    add(fileVideo)
                    add(youtubeVideo)
                    add(youtube360Video)
                    add(youtubeHlsVideo)
                    addAll(uris.map { Image(uri = it.toString()) })
                }
            }.startActivity()
        }

        youtubeVideos.setOnClickListener {
            MediaGalleryActivity.Builder.with(this@MainActivity) {
                autoPlay = true
                isBlurrable = true
                isZoomable = true
                showVideoControls = true
                autoPlay = true
                media = listOf(youtubeVideo, youtube360Video, youtubeHlsVideo)
            }.startActivity()
        }
    }

    // region CompositeDisposableHolder

    var subscription = CompositeDisposable()

    private fun disposeCompositeDisposable() {
        if (!subscription.isDisposed)
            subscription.dispose()
    }

    // endregion

    override fun onDestroy() {
        disposeCompositeDisposable()
        super.onDestroy()
    }
}

fun String.parseAssetFile(): Uri = Uri.parse("file:///android_asset/$this")