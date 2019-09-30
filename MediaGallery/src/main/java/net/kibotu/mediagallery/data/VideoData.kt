package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Video(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val type: Type = Type.ASSETS
) : MediaData {

    @IgnoredOnParcel
    @Transient
    internal var progress: Long = -1

    enum class Type {
        ASSETS, HLS
    }
}