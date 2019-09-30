package net.kibotu.mediagallery.demo

import android.Manifest
import androidx.fragment.app.Fragment
import com.exozet.android.core.base.CompositeDisposableHolder
import com.exozet.android.core.utils.MathExtensions
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.addTo
import net.kibotu.logger.Logger.logw
import kotlin.random.Random


inline fun <reified T> T.requestReadExternalStoragePermission(crossinline block: (permission: Permission) -> Unit) where T : Fragment, T : CompositeDisposableHolder = RxPermissions(this)
    .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
    .subscribe({
        block(it)
    }, {
        logw { "permission $it" }
    }).addTo(subscription)


val categories by lazy {
    listOf("abstract", "animals", "business", "cats", "city", "food", "nightlife", "fashion", "people", "nature", "sports", "technics", "transport")
}

fun createRandomImageUrl(): String {

    val maxWidth = 1080
    val maxHeight = 1920

    val landscape = Random.nextBoolean()
    val endpoint = Random.nextBoolean()

    val width = MathExtensions.random(maxWidth, maxHeight)
    val height = MathExtensions.random(maxWidth, maxHeight)

    return "https://lorempixel.com/${categories.random()}/%d/%d".format(if (landscape) width else height, if (landscape) height else width)
}