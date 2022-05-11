package net.kibotu.mediagallery.data

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
@Parcelize
data class Video(
    val id: String,
    var uri: Uri,
    val type: Type = Type.FILE,
    val enable360: Boolean = false
) : Media {

    constructor(id: String = UUID.randomUUID().toString(), uri: String, type: Type, enable360: Boolean = false) : this(id, uri.toUri(), type, enable360)

    @IgnoredOnParcel
    @Transient
    internal var progress: Long = -1

    enum class Type {
        /**
         * prepends file:///android_asset/
         */
        ASSETS,

        /**
         * raw uri, assumes it's a local file
         */
        FILE,

        /**
         * hsl stream uri
         */
        HLS,

        /**
         * dash stream uri
         */
        DASH,

        /**
         * retrieves video url by youtube video id, video requires to be publicly available
         */
        YOUTUBE
    }
}