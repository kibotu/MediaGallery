package net.kibotu.mediagallery.demo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.utils.MathExtensions
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.mediagallery.MediaData
import net.kibotu.mediagallery.MediaGalleryActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogger(LogcatLogger())

        // [] list of media objects
        // [] images
        // [] streaming urls
        // [] click listener
        // [] zoomable
        // [] player controls
        // [] blurry

        image_gallery.onClick {
            val uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { MediaData(uri = it) }
                preload = media.size
            }.startActivity()
        }

        video_gallery.onClick {
            val uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { MediaData(uri = it) }
                preload = media.size.coerceAtMost(10)
            }.startActivity()
        }

        mixed_gallery.onClick {
            val uris = (0 until 100).map { Uri.parse(createRandomImageUrl()) }
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { MediaData(uri = it) }
            }.startActivity()
        }
    }
}


fun createRandomImageUrl(): String {

    val maxWidth = 1080
    val maxHeight = 1920

    val landscape = Random.nextBoolean()
    val endpoint = Random.nextBoolean()

    val width = MathExtensions.random(maxWidth, maxHeight)
    val height = MathExtensions.random(maxWidth, maxHeight)

    return (if (endpoint)
        "https://lorempixel.com/%d/%d/"
    else
        "https://picsum.photos/%d/%d/")
        .format(if (landscape) width else height, if (landscape) height else width)
}