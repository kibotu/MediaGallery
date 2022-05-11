package net.kibotu.mediagallery.internal.presenter
//
//
//class VideoPresenter(
//    private val showVideoControls: Boolean,
//    private val showVideoControlsTimeOut: Int,
//    private val autoPlay: Boolean,
//    private val showPlayPauseButton: Boolean = false
//) : Presenter<Video, MediaGalleryVideoPresenterBinding>(R.layout.media_gallery_video_presenter, MediaGalleryVideoPresenterBinding::bind) {
//
//    override fun bindViewHolder(viewBinding: MediaGalleryVideoPresenterBinding, viewHolder: RecyclerView.ViewHolder, item: PresenterViewModel<Video>, payloads: MutableList<Any>?): Unit =
//        with(viewBinding) {
//
//            log("bindViewHolder ${viewHolder.bindingAdapterPosition} ${item.model}")
//
//            playerView.useController = showVideoControls
//            playerView.controllerShowTimeoutMs = showVideoControlsTimeOut
//
//            with(viewHolder as VideoViewHolder) {
//                autoPlay = this@VideoPresenter.autoPlay
//                uri = item.model.uri
//                currentProgress = item.model.progress
//                type = item.model.type
//                progress = {
//                    item.model.progress = it
//                }
//
//                playerView.onClick {
//                    isPlaying = !isPlaying
//                }
//
//                // todo prettier play/pause button
//                playPauseButton(this)
//            }
//        }
//
//    private fun MediaGalleryVideoPresenterBinding.playPauseButton(vh: VideoViewHolder) {
//
//        playPauseAnimation.isGone = !showPlayPauseButton
//        playPause.isGone = !showPlayPauseButton
//
//        if (!showPlayPauseButton) {
//            return
//        }
//
//        playPauseAnimation.progress = if (vh.isPlaying) 0f else 0.5f
//
//        playPause.onClick {
//
//            vh.isPlaying = !vh.isPlaying
//
//            if (vh.isPlaying) {
//                playPauseAnimation.setMinAndMaxProgress(0.5f, 1f)
//            } else {
//                playPauseAnimation.setMinAndMaxProgress(0f, 0.5f)
//            }
//            playPauseAnimation.playAnimation()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = VideoViewHolder(parent, layout)
//}