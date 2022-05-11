//package net.kibotu.mediagallery.legacy.internal.presenter
//
////import com.commit451.youtubeextractor.Stream
////import com.commit451.youtubeextractor.YouTubeExtractor
////import com.google.android.exoplayer2.ExoPlayerFactory
//import android.net.Uri
//import android.util.Log
//import android.view.ViewGroup
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SeekParameters
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.source.hls.HlsMediaSource
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import io.reactivex.disposables.Disposable
//import net.kibotu.android.recyclerviewpresenter.RecyclerViewHolder
//import net.kibotu.mediagallery.BuildConfig
//import net.kibotu.mediagallery.R
//import net.kibotu.mediagallery.legacy.data.Video
//import net.kibotu.mediagallery.legacy.internal.parseAssetFile
//import net.kibotu.mediagallery.legacy.internal.parseExternalStorageFile
//import net.kibotu.mediagallery.legacy.internal.parseInternalStorageFile
//import java.util.*
//import kotlin.math.abs
//
//
//internal class VideoViewHolder(parent: ViewGroup, layout: Int) : RecyclerViewHolder(parent, layout) {
//
//    private val application
//        get() = itemView.context.applicationContext
//
//    private val playerView: PlayerView
//        get() = itemView.findViewById(R.id.player_view)
//
//    private var player: SimpleExoPlayer?
//        set(value) {
//            playerView.player = value
//        }
//        get() = playerView.player as? SimpleExoPlayer?
//
//    private val enableLogging = BuildConfig.DEBUG
//
//    private fun logv(block: () -> String?) {
//        if (enableLogging)
//            Log.v(VideoViewHolder::class.java.simpleName, "${block()}")
//    }
//
//    private var disposable: Disposable? = null
//
////    private val extractor by lazy { YouTubeExtractor.Builder().build() }
//
//    init {
//        logv { "init" }
//    }
//
//    override fun onViewAttachedToWindow() {
//        logv { "onViewAttachedToWindow" }
//        setupExoPlayer()
//        prepare()
//        start()
//    }
//
//    override fun onViewDetachedFromWindow() {
//        logv { "onViewDetachedFromWindow" }
//        clear()
//    }
//
//    override fun onViewRecycled() {
//        logv { "onViewRecycled" }
//        clear()
//        super.onViewRecycled()
//    }
//
//    override fun onFailedToRecycleView(): Boolean {
//        logv { "onFailedToRecycleView" }
//        clear()
//        return super.onFailedToRecycleView()
//    }
//
//    private fun setupExoPlayer() {
//        logv { "setupExoPlayer" }
//        if (player == null) {
//            player = SimpleExoPlayer.Builder(playerView.context)
//                .setTrackSelector(DefaultTrackSelector(AdaptiveTrackSelection.Factory()))
//                .build()
//        }
//    }
//
//    private fun prepare() {
//        logv { "prepare uri=$uri" }
//
//        val defaultDataSourceFactory = DefaultDataSourceFactory(application, "exoplayer")
//
//        val mediaSource = when {
//            type == Video.Type.ASSETS -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri.toString().parseAssetFile())
//            type == Video.Type.EXTERNAL_STORAGE -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri.toString().parseExternalStorageFile())
//            type == Video.Type.INTERNAL_STORAGE -> ProgressiveMediaSource.Factory(defaultDataSourceFactory)
//                .createMediaSource(uri.toString().parseInternalStorageFile(itemView.context.applicationContext))
//            type == Video.Type.HLS -> HlsMediaSource.Factory(defaultDataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(uri ?: return)
//            type == Video.Type.YOUTUBE && uris.containsKey(uri) -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uris[uri] ?: return)
////            type == Video.Type.YOUTUBE -> {
//
////                disposable = extractor.extract(uri.toString())
////                    .subscribeOn(Schedulers.io())
////                    .observeOn(AndroidSchedulers.mainThread())
////                    .subscribe({
////
////                        val videoStreams = it.streams.filterIsInstance<Stream.VideoStream>()
////
////                        videoStreams.forEach { logv { "$it" } }
////
////                        val mp4s = videoStreams.filter { it.format == "MPEG4" }
////                        val videos = if (mp4s.any { !it.isVideoOnly }) mp4s.filter { !it.isVideoOnly } else mp4s
////
////                        val playerHeight = playerView.height
////
////                        val resolutions = videos.map { it.resolution.replace("p", "").toInt() }.distinct()
////                        val closest = resolutions.closestValue(playerHeight)
////
////                        var closestVideo = videos.firstOrNull { it.resolution == "${closest}p" }
////
////                        logv { "playerHeight=${playerHeight} closest=$closest closestVideo=$closestVideo" }
////
////                        closestVideo = closestVideo ?: mp4s.firstOrNull()
////
////                        val uri = closestVideo?.url?.toUri() ?: return@subscribe
////
////                        logv { "play $it" }
////
////                        // add to cache
////                        uris[uri] = uri
////
//////                        val source = HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri)
////                        val source = ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri)
////                        player?.prepare(source)
////
////                    }, { logv { "$it" } })
////
////                null
////
////            }
//            /* Video.Type.FILE */ else -> ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri ?: return)
//        }
//
//        player?.repeatMode = Player.REPEAT_MODE_ALL
//        player?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
//        player?.prepare(mediaSource ?: return)
//
//    }
//
//    var type: Video.Type = Video.Type.FILE
//
//    var uri: Uri? = null
//
//    var progress: ((Long) -> Unit)? = null
//
//    var currentProgress: Long = -1
//
//    var autoPlay: Boolean = true
//
//    var isPlaying: Boolean
//        set(value) {
//            player?.playWhenReady = value
//        }
//        get() = player?.playWhenReady == true
//
//    private fun start() {
//        logv { "start from currentProgress=$currentProgress" }
//        player?.seekTo(currentProgress.coerceAtLeast(0))
//        if (autoPlay) {
//            isPlaying = true
//            player?.playWhenReady = true
//        }
//    }
//
//    fun stop() {
//        logv { "stop at currentProgress=$currentProgress" }
//        currentProgress = player?.currentPosition ?: -1
//        progress?.invoke(currentProgress)
//        isPlaying = false
//    }
//
//    fun clear() {
//        stop()
//        logv { "clear" }
//        player?.stop()
//        player?.release()
//        player = null
//        progress = null
//
//        if (disposable?.isDisposed == false) {
//            disposable?.dispose()
//        }
//        disposable = null
//    }
//
//    companion object {
//        val uris = WeakHashMap<Uri, Uri>()
//
//        private fun List<Int>.closestValue(value: Int) = minByOrNull { abs(value.minus(it)) }
//    }
//}