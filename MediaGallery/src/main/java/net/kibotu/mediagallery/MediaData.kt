package net.kibotu.mediagallery

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MediaData(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri
) : Parcelable