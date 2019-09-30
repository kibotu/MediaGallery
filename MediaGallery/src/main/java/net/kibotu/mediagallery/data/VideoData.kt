package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Video(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val type: Type = Type.FILE
) : MediaData {

    constructor(id: String = UUID.randomUUID().toString(), uri: String, type: Type) : this(id, Uri.parse(uri), type)

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
        HLS
    }
}