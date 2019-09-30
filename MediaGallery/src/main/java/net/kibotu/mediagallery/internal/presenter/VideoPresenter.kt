package net.kibotu.mediagallery.internal.presenter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.media_gallery_video_presenter.view.*
import net.kibotu.android.recyclerviewpresenter.Adapter
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.Video
import net.kibotu.mediagallery.internal.log
import net.kibotu.mediagallery.internal.onClick

class VideoPresenter(
    private val showVideoControls: Boolean,
    private val showVideoControlsTimeOut: Int,
    private val autoPlay: Boolean,
    private val showPlayPauseButton: Boolean = false
) : Presenter<Video>() {

    override val layout = R.layout.media_gallery_video_presenter

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: PresenterModel<Video>, position: Int, payloads: MutableList<Any>?, adapter: Adapter) {

        log { "bindViewHolder $position ${item.model}" }

        with(viewHolder.itemView) {

            player_view.useController = showVideoControls
            player_view.controllerShowTimeoutMs = showVideoControlsTimeOut

            with(viewHolder as VideoViewHolder) {
                autoPlay = autoPlay
                uri = item.model.uri
                currentProgress = item.model.progress
                type = item.model.type
                progress = {
                    item.model.progress = it
                }

                player_view.onClick {
                    isPlaying = !isPlaying
                }

                // todo prettier play/pause button
                playPauseButton(this)
            }
        }
    }

    private fun View.playPauseButton(vh: VideoViewHolder) {

        playPauseAnimation.isGone = !showPlayPauseButton
        playPause.isGone = !showPlayPauseButton

        if (!showPlayPauseButton) {
            return
        }

        playPauseAnimation.progress = if (vh.isPlaying) 0f else 0.5f

        playPause.onClick {

            vh.isPlaying = !vh.isPlaying

            if (vh.isPlaying) {
                playPauseAnimation.setMinAndMaxProgress(0.5f, 1f)
            } else {
                playPauseAnimation.setMinAndMaxProgress(0f, 0.5f)
            }
            playPauseAnimation.playAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = VideoViewHolder(parent, layout)
}