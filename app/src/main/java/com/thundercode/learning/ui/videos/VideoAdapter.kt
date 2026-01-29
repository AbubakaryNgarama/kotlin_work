package com.thundercode.learning.ui.videos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thundercode.learning.data.models.Video
import com.thundercode.learning.databinding.ItemVideoBinding

class VideoAdapter(
    private var videos: List<Video>,
    private val onVideoClick: ((Video) -> Unit)? = null,
    private val onEditClick: ((Video) -> Unit)? = null,
    private val onDeleteClick: ((Video) -> Unit)? = null,
    private val showActions: Boolean = false
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    fun updateVideos(newVideos: List<Video>) {
        videos = newVideos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size

    inner class VideoViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.tvTitle.text = video.title
            binding.tvUploader.text = video.uploader_name ?: "Unknown"
            binding.tvViews.text = "${video.views_count} views"

            if (showActions) {
                binding.btnEdit.visibility = android.view.View.VISIBLE
                binding.btnDelete.visibility = android.view.View.VISIBLE

                binding.btnEdit.setOnClickListener {
                    onEditClick?.invoke(video)
                }

                binding.btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(video)
                }
            } else {
                binding.btnEdit.visibility = android.view.View.GONE
                binding.btnDelete.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener {
                onVideoClick?.invoke(video)
            }
        }
    }
}