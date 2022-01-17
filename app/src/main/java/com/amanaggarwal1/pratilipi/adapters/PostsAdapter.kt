package com.amanaggarwal1.pratilipi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.amanaggarwal1.pratilipi.R
import com.amanaggarwal1.pratilipi.Utils.hideKeyboard
import com.amanaggarwal1.pratilipi.databinding.PostItemBinding
import com.amanaggarwal1.pratilipi.fragments.HomeFragmentDirections
import com.amanaggarwal1.pratilipi.model.Post

class PostsAdapter: ListAdapter<Post, PostsAdapter.PostViewHolder>(DiffUtilCallback()){

    // function to create view holders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_item, parent, false)
        )
    }

    // function to bind views and view holders
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position).let { post ->
            holder.apply {


                title.text = post.title
                description.text = post.description
                date.text = post.date
                if(post.imageUri == "") image.visibility = View.GONE
                else{
                    image.visibility = View.VISIBLE
                    image.setImageURI(post.imageUri.toUri())
                }

                // click listener for opening posts on clicking on title of post
                title.setOnClickListener{
                    val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
                        .setPost(post)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }

                // click listener for opening posts on clicking on title of post
                description.setOnClickListener{
                    val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
                        .setPost(post)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }

                // click listener for opening posts on clicking on title of post
                image.setOnClickListener{
                    val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
                        .setPost(post)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }

                // click listener for opening posts on clicking on title of post
                date.setOnClickListener{
                    val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
                        .setPost(post)
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action)
                }
            }

        }
    }



    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = PostItemBinding.bind(itemView)
        val title = binding.postItemTitle
        val description = binding.postDescription
        val date = binding.postDate
        val image = binding.postImage

    }


}

