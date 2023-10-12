package com.aghyksa.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghyksa.storyapp.databinding.ItemStoryBinding
import com.aghyksa.storyapp.model.Story
import com.aghyksa.storyapp.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StoryAdapter(val onItemClickCallback: OnItemClickCallback =
    object : OnItemClickCallback {override fun onItemClicked(data: Story) {}})
    : RecyclerView.Adapter<StoryAdapter.UserViewHolder>() {

    private val stories = ArrayList<Story>()

    fun setStories(stories:List<Story>){
        this.stories.clear()
        this.stories.addAll(stories)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val bind: ItemStoryBinding) : RecyclerView.ViewHolder(bind.root){
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder((view))
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Story)
    }
}