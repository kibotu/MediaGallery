package net.kibotu.mediagallery.internal.presenter

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.core.net.toUri
import com.commit451.youtubeextractor.YouTubeExtractor
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.kibotu.android.recyclerviewpresenter.RecyclerViewHolder
import net.kibotu.mediagallery.BuildConfig
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.internal.parseAssetFile
import net.kibotu.mediagallery.internal.parseExternalStorageFile
import net.kibotu.mediagallery.internal.parseInternalStorageFile


internal class VideoViewHolder(parent: ViewGroup, layout: Int) : RecyclerViewHolder(parent, layout) {

    private val application
        get() = itemView.context.applicationContext

    private val playerView: PlayerView
        get() = itemView.findViewById(R.id.player_view)

    private var player: SimpleExoPlayer?
        set(value) {
            playerView.player = value
        }
        get() = playerView.player as? SimpleExoPlayer?

    private val enableLogging = BuildConfig.DEBUG

    private fun logv(block: () -> String?) {
        if (enableLogging)
            Log.v(VideoViewHolder::class.java.simpleName, "${block()}")
    }

    private var disposable: Disposable? = null

    private val extractor by lazy { YouTubeExtractor.Builder().build() }

    init {
        logv { "init" }
    }

    override fun onViewAttachedToWindow() {
        logv { "onViewAttachedToWindow" }
        setupExoPlayer()
        prepare()
        start()
    }

    override fun onViewDetachedFromWindow() {
        logv { "onViewDetachedFromWindow" }
        clear()
    }

    override fun onViewRecycled() {
        logv { "onViewRecycled" }
        clear()
        super.onViewRecycled()
    }

    override fun onFailedToRecycleView(): Boolean {
        logv { "onFailedToRecycleView" }
        clear()
        return super.onFailedToRecycleView()
    }

    private fun setupExoPlayer() {
        logv { "setupExoPlayer" }
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(application, DefaultTrackSelector(AdaptiveTrackSelection.Factory()))
        }
    }

    private fun prepare() {
        logv { "prepare uri=$uri" }

        val defaultDataSourceFactory = DefaultDataSourceFactory(application, "exoplayer")

        val mediaSource = when (type) {
            Video.Type.ASSETS -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri.toString().parseAssetFile())
            Video.Type.EXTERNAL_STORAGE -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri.toString().parseExternalStorageFile())
            Video.Type.INTERNAL_STORAGE -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri.toString().parseInternalStorageFile(itemView.context.applicationContext))
            Video.Type.HLS -> HlsMediaSource.Factory(defaultDataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(uri)
            Video.Type.YOUTUBE -> {

                disposable = extractor.extract(uri.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        logv { "$it" }

                        val uri = it.videoStreams.firstOrNull { it.format == "MPEG4" }?.url?.toUri() ?: return@subscribe

                        logv { "play $uri" }

//                        val source = HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri)
                        val source = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri)
                        player?.prepare(source)

                    }, {

                        logv { "$it" }
                    })

                null

            }
            /* Video.Type.FILE */ else -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri)
        }

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.seekParameters = SeekParameters.CLOSEST_SYNC
        player!!.prepare(mediaSource ?: return)

    }

    var type: Video.Type = Video.Type.ASSETS

    var uri: Uri? = null

    var progress: ((Long) -> Unit)? = null

    var currentProgress: Long = -1

    var autoPlay: Boolean = true

    var isPlaying: Boolean
        set(value) {
            player?.playWhenReady = value
        }
        get() = player?.playWhenReady == true

    private fun start() {
        logv { "start from currentProgress=$currentProgress" }
        player!!.seekTo(currentProgress.coerceAtLeast(0))
        if (autoPlay) {
            isPlaying = true
            player!!.playWhenReady = true
        }
    }

    fun stop() {
        logv { "stop at currentProgress=$currentProgress" }
        currentProgress = player?.currentPosition ?: -1
        progress?.invoke(currentProgress)
        isPlaying = false
    }

    fun clear() {
        stop()
        logv { "clear" }
        player?.stop()
        player?.release()
        player = null
        progress = null

        if (disposable?.isDisposed == false) {
            disposable?.dispose()
        }
        disposable = null
    }
}
