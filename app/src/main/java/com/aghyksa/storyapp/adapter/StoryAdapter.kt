package com.aghyksa.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aghyksa.storyapp.databinding.ItemStoryBinding
import com.aghyksa.storyapp.model.Story
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StoryAdapter(val onItemClickCallback: OnItemClickCallback =
    object : OnItemClickCallback {override fun onItemClicked(data: Story) {}})
    : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val bind: ItemStoryBinding) : RecyclerView.ViewHolder(bind.root){
        fun bind(story: Story){
            bind.apply {
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivStory)
                tvName.text = story.name
                tvDesc.text = story.description
                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(story)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder((view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Story)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}