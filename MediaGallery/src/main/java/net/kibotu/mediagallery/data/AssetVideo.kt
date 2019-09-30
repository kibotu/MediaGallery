package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class AssetVideo(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri
) : VideoData {

    @IgnoredOnParcel
    @Transient
    internal var progress: Long = -1
}