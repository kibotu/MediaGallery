package net.kibotu.mediagallery.internal.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import me.jessyan.progressmanager.ProgressManager
import net.kibotu.mediagallery.BuildConfig
import net.kibotu.mediagallery.internal.log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.util.concurrent.TimeUnit


@GlideModule
class GlideConfiguration : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        log { "registerComponents" }
        val client = ProgressManager.getInstance().with(createOkHttpClient()).build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }
}

private fun createOkHttpClient(): OkHttpClient.Builder = OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(15, TimeUnit.SECONDS)
    .addInterceptor(createHttpLoggingInterceptor { BuildConfig.DEBUG })


private fun createHttpLoggingInterceptor(enableLogging: () -> Boolean): HttpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        log { message }
    }
}).apply {
    level = if (enableLogging())
        HttpLoggingInterceptor.Level.BODY
    else
        HttpLoggingInterceptor.Level.NONE
}