package net.kibotu.mediagallery.internal.presenter

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import net.kibotu.android.recyclerviewpresenter.RecyclerViewHolder
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Video


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

    private val enableLogging = false

    private fun logv(block: () -> String?) {
        if (enableLogging)
            Log.v(VideoViewHolder::class.java.simpleName, "${block()}")
    }

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
            Video.Type.ASSETS -> ProgressiveMediaSource.Factory(defaultDataSourceFactory)
            Video.Type.HLS -> HlsMediaSource.Factory(defaultDataSourceFactory).setAllowChunklessPreparation(true)
            else -> HlsMediaSource.Factory(defaultDataSourceFactory)
        }.createMediaSource(uri)

        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare(mediaSource)

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
    }
}
