package net.kibotu.mediagallery.demo

import com.exozet.android.core.utils.MathExtensions
import kotlin.random.Random

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