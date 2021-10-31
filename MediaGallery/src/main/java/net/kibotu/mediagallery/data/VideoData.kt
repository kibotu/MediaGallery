package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Video(
    val id: String = UUID.randomUUID().toString(),
    var uri: Uri,
    val type: Type = Type.FILE,
    val enable360: Boolean = false
) : MediaData {

    constructor(id: String = UUID.randomUUID().toString(), uri: String, type: Type, enable360: Boolean = false) : this(id, Uri.parse(uri), type, enable360)

    @IgnoredOnParcel
    @Transient
    internal var progress: Long = -1

    enum class Type {
        /**
         * prepends file:///android_asset/
         */
        ASSETS,
        /**
         * prepends context.applicationContext.filesDir.absolutePath
         */
        EXTERNAL_STORAGE,
        /**
         * prepends Environment.getExternalStorageDirectory()
         */
        INTERNAL_STORAGE,
        /**
         * raw uri, assumes it's a local file
         */
        FILE,
        /**
         * hsl stream uri
         */
        HLS,
        /**
         * retrieves video url by youtube video id, video requires to be publicly available
         */
        YOUTUBE
    }
}