package net.kibotu.mediagallery.internal.progress

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import me.jessyan.progressmanager.ProgressManager
import net.kibotu.mediagallery.BuildConfig
import net.kibotu.mediagallery.internal.extensions.log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
@GlideModule
class GlideConfiguration : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        log("registerComponents")
        val client = ProgressManager.getInstance().with(createOkHttpClient()).build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }
}

private fun createOkHttpClient(): OkHttpClient.Builder = OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(15, TimeUnit.SECONDS)
    .addInterceptor(
        HeaderTypeInterceptor(
            mapOf(
                "Accept" to "*/*",
                "Cache-Control" to "no-cache",
                "Accept-Encoding" to "gzip, deflate",
                "Connection" to "keep-alive"
            )
        )
    )
    .addInterceptor(createHttpLoggingInterceptor { BuildConfig.DEBUG })

private fun createHttpLoggingInterceptor(enableLogging: () -> Boolean): HttpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Log.v("HttpLoggingInterceptor", message)
    }
}).apply {
    level = if (enableLogging())
        BODY
    else
        NONE
}

private class HeaderTypeInterceptor(private val headers: Map<String, String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain
        .proceed(with(
            chain.request()
                .newBuilder()
        ) {
            headers.forEach {
                header(it.key, it.value)
            }
            build()
        })
}