package net.kibotu.mediagallery.internal.video

import android.content.Context
import android.net.Uri
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import net.kibotu.android.recyclerviewpresenter.RecyclerViewHolder
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.internal.extensions.log
import net.kibotu.mediagallery.internal.extensions.parseAssetFile
import net.kibotu.mediagallery.internal.extensions.textOrGone


/**
 * Created by [Jan Rabe](https://kibotu.net).
 */
internal class VideoViewHolder(parent: ViewGroup, layout: Int) : RecyclerViewHolder(parent, layout) {

    private val application
        get() = itemView.context.applicationContext

    private val playerView: StyledPlayerView
        get() = itemView.findViewById(R.id.player_view)

    private val title: TextView
        get() = itemView.findViewById(R.id.title)

    private val author: TextView
        get() = itemView.findViewById(R.id.author)

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
        val uri = uri ?: return
        when (type) {
            Video.Type.ASSETS -> onVideo(uri.toString().parseAssetFile())
            Video.Type.HLS -> onHlsVideo(uri)
            Video.Type.DASH -> onDashVideo(uri)
            Video.Type.YOUTUBE -> onYoutubeVideo(uri)
            /* Video.Type.FILE */ else -> onVideo(uri)
        }
    }

    private fun onVideo(uri: Uri) {
        log("onVideo $uri")
        val dataSource = DefaultDataSource.Factory(application)
        val mediaSource = ProgressiveMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(uri))
        player?.repeatMode = Player.REPEAT_MODE_ALL
        player?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        player?.setMediaSource(mediaSource)
        player?.prepare()
    }

    private fun onYoutubeVideo(uri: Uri) {
        log("onYoutubeVideo $uri")
        Extractor(application) { ytFiles, vMeta ->

            val map = ytFiles.toMap()

            val channel = vMeta?.channelId
            val videoId = vMeta?.videoId
            val viewCount = vMeta?.viewCount

            author.textOrGone = vMeta?.author
            title.textOrGone = vMeta?.title

            map.forEach {
                log("${it.key} -> ${it.value}")
            }

            val video = map
                .filter { it.value.format.ext == "mp4" }
                .maxByOrNull { it.value.format.height }
                ?.value ?: return@Extractor

            if (video.format.isHlsContent) onHlsVideo(video.url.toUri())
            // if (video.format.isDashContainer) onDashVideo(video.url.toUri()) todo, does not work atm
            else onVideo(video.url.toUri())

        }.extract(uri.toString())
    }

    private class Extractor(context: Context, val onComplete: (ytFiles: SparseArray<YtFile>, vMeta: VideoMeta?) -> Unit) : YouTubeExtractor(context) {

        override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
            if (ytFiles != null) onComplete(ytFiles, vMeta)
        }
    }

    private fun onHlsVideo(uri: Uri) {
        log("onHlsVideo $uri")
        val dataSource = DefaultHttpDataSource.Factory()
        val mediaSource = HlsMediaSource.Factory(dataSource).setAllowChunklessPreparation(true).createMediaSource(MediaItem.fromUri(uri))
        player?.repeatMode = Player.REPEAT_MODE_ALL
        player?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        player?.setMediaSource(mediaSource)
        player?.prepare()
    }

    private fun onDashVideo(uri: Uri) {
        log("onDashVideo $uri")
        val dataSource = DefaultHttpDataSource.Factory()
        val mediaSource: MediaSource = DashMediaSource.Factory(dataSource).createMediaSource(MediaItem.fromUri(uri))
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

private inline fun <reified E> SparseArray<E>.toMap(): Map<Int, E> = buildMap {
    for (i in 0 until size()) {
        put(keyAt(i), valueAt(i))
    }
}
