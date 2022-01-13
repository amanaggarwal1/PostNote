package com.amanaggarwal1.pratilipi.adapters

import androidx.recyclerview.widget.DiffUtil
import com.amanaggarwal1.pratilipi.model.Post

class DiffUtilCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }
}