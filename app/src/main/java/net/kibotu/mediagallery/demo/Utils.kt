@file:JvmName("FakeDataGenerator")

package net.kibotu.mediagallery.demo

import java.text.MessageFormat.format
import kotlin.math.roundToInt
import kotlin.random.Random

fun createRandomImageUrl(): String {

    val landscape = Random.nextBoolean()
    val endpoint = false // randomBoolean()

    val width = randomRange(300, 400)
    val height = randomRange(200, 300)

    return format(
        if (endpoint)
            "https://lorempixel.com/{0}/{1}/"
        else
            "https://picsum.photos/{0}/{1}/",
        if (landscape) width else height, if (landscape) height else width
    )
}

fun randomRange(start: Int = 0, end: Int): Int = (Random.nextFloat() * (end - start) + start).roundToInt()