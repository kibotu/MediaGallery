package net.kibotu.mediagallery.demo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.extensions.parseAssetFile
import com.exozet.android.core.utils.MathExtensions
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.logger.Logger.logv
import net.kibotu.mediagallery.MediaGalleryActivity
import net.kibotu.mediagallery.data.AssetVideo
import net.kibotu.mediagallery.data.Image
import net.kibotu.mediagallery.data.MediaData
import net.kibotu.resourceextension.screenHeightPixels
import net.kibotu.resourceextension.screenWidthPixels
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogger(LogcatLogger())

        logv { "window=${screenWidthPixels}x$screenHeightPixels " }

        // [] list of imageMedia objects
        // [x] images
        // [] streaming urls
        // [] click listener
        // [x] zoomable
        // [x] translatable
        // [] player controls
        // [x] blurry
        // [x] crossfade background
        // [x] quit button
        // [] youtube videos
        // [] swipe down to quit
        // [x] preload
        // [] preload progressbar
        // [] viewpager indicators

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

        val assetVideo = AssetVideo(uri = "walkaround_with_additional_iframes.mp4".parseAssetFile())
        val hlsVideo = AssetVideo(uri = Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"))

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
                media = uris.map { Image(uri = it) }
                preload = media.size.coerceAtMost(10)
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
                media = mutableListOf<MediaData>().apply {
                    addAll(uris.map { Image(uri = it) })
                    add(3, assetVideo)
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
            }.startActivity()
        }

        mixed_gallery.performClick()
    }
}

val categories by lazy {
    listOf("abstract", "animals", "business", "cats", "city", "food", "nightlife", "fashion", "people", "nature", "sports", "technics", "transport")
}

fun createRandomImageUrl(): String {

    val maxWidth = 1080
    val maxHeight = 1920

    val landscape = Random.nextBoolean()
    val endpoint = Random.nextBoolean()

    val width = MathExtensions.random(maxWidth, maxHeight)
    val height = MathExtensions.random(maxWidth, maxHeight)

    return "https://lorempixel.com/${categories.random()}/%d/%d".format(if (landscape) width else height, if (landscape) height else width)
}