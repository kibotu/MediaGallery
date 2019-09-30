package net.kibotu.mediagallery.internal.presenter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.media_gallery_video_presenter.view.*
import net.kibotu.android.recyclerviewpresenter.Adapter
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterModel
import net.kibotu.mediagallery.R
import net.kibotu.mediagallery.data.AssetVideo
import net.kibotu.mediagallery.internal.log

class VideoPresenter(
    private val showVideoControls: Boolean,
    private val showVideoControlsTimeOut: Int,
    private val autoPlay: Boolean
) : Presenter<AssetVideo>() {

    override val layout = R.layout.media_gallery_video_presenter

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder, item: PresenterModel<AssetVideo>, position: Int, payloads: MutableList<Any>?, adapter: Adapter) {

        log { "bindViewHolder $position ${item.model}" }

        with(viewHolder.itemView) {

            player_view.useController = showVideoControls
            player_view.controllerShowTimeoutMs = showVideoControlsTimeOut

            val vh = viewHolder as VideoViewHolder
            vh.autoPlay = autoPlay
            vh.uri = item.model.uri
            vh.progress = {
                item.model.progress = it
            }
            vh.currentProgress = item.model.progress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = VideoViewHolder(parent, layout)
}