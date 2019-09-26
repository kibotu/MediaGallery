package net.kibotu.mediagallery.demo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exozet.android.core.extensions.onClick
import com.exozet.android.core.misc.createRandomImageUrl
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.logger.LogcatLogger
import net.kibotu.logger.Logger
import net.kibotu.mediagallery.MediaData
import net.kibotu.mediagallery.MediaGalleryActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.addLogger(LogcatLogger())

        // [] list of media objects
        // [] images
        val uris = (0 until 10).map { Uri.parse(createRandomImageUrl()) }
        // [] streaming urls
        // [] click listener
        // [] zoomable
        // [] player controls
        // [] blurry

        image_gallery.onClick {
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { MediaData(uri = it) }
            }.startActivity()
        }

        video_gallery.onClick {
            MediaGalleryActivity.Builder.with(this) {
                autoPlay = true
                isBlurrable = true
                isTranslatable = true
                isZoomable = true
                showVideoControls = true
                media = uris.map { MediaData(uri = it) }
            }.startActivity()
        }

        mixed_gallery.onClick {
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