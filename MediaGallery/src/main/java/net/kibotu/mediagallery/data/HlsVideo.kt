package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class HlsVideo(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri
) : VideoData