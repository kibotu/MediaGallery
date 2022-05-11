package net.kibotu.mediagallery.internal.video

import android.net.Uri
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import net.kibotu.android.recyclerviewpresenter.RecyclerViewHolder
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.internal.extensions.log
import net.kibotu.mediagallery.internal.extensions.parseAssetFile

/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
internal class VideoViewHolder(parent: ViewGroup, layout: Int) : RecyclerViewHolder(parent, layout) {

    private val application
        get() = itemView.context.applicationContext

    private val playerView: StyledPlayerView
        get() = itemView.findViewById(R.id.player_view)

    private var player: ExoPlayer?
        set(value) {
            playerView.player = value
        }
        get() = playerView.player as? ExoPlayer?

    override fun onViewAttachedToWindow() {
        setupExoPlayer()
        prepare()
        start()
    }

    override fun onViewDetachedFromWindow() {
        clear()
    }

    override fun onViewRecycled() {
        clear()
        super.onViewRecycled()
    }

    override fun onFailedToRecycleView(): Boolean {
        clear()
        return super.onFailedToRecycleView()
    }

    private fun setupExoPlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(playerView.context)
                .setTrackSelector(DefaultTrackSelector(playerView.context, AdaptiveTrackSelection.Factory()))
                .build()
        }
    }

    private fun prepare() {
        val defaultDataSourceFactory = DefaultDataSource.Factory(application)

        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        val mediaSource = when {
            type == Video.Type.ASSETS -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(uri.toString().parseAssetFile()))
            type == Video.Type.HLS -> HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(MediaItem.fromUri(uri ?: return))
            /* Video.Type.FILE */ else -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(uri ?: return))
        }

        player?.repeatMode = Player.REPEAT_MODE_ALL
        player?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        player?.setMediaSource(mediaSource)
        player?.prepare()
    }

    var type: Video.Type = Video.Type.FILE

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
        log("start from currentProgress=$currentProgress")
        player?.seekTo(currentProgress.coerceAtLeast(0))
        if (autoPlay) {
            isPlaying = true
            player?.playWhenReady = true
        }
    }

    fun stop() {
        log("stop at currentProgress=$currentProgress")
        currentProgress = player?.currentPosition ?: -1
        progress?.invoke(currentProgress)
        isPlaying = false
    }

    fun clear() {
        stop()
        player?.stop()
        player?.release()
        player = null
        progress = null
    }
}