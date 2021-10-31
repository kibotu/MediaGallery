package net.kibotu.mediagallery.data

import android.net.Uri
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Image(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri
) : MediaData {
    constructor(id: String = UUID.randomUUID().toString(), uri: String) : this(id, Uri.parse(uri))
}
