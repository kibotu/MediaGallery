package net.kibotu.mediagallery.data

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
@Parcelize
data class Image(
    val id: String,
    val uri: Uri
) : Media {

    constructor(id: String = UUID.randomUUID().toString(), uri: String) : this(id, uri.toUri())

}