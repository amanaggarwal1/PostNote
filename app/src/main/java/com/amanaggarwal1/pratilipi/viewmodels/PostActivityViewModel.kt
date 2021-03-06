package com.amanaggarwal1.pratilipi.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amanaggarwal1.pratilipi.model.Post
import com.amanaggarwal1.pratilipi.repository.PostRepository
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostActivityViewModel(private val repository: PostRepository): ViewModel(){

    var imageUri = MutableLiveData<Uri?>()


    fun savePost(newPost: Post) = viewModelScope.launch(Dispatchers.IO) {
        repository.addPost(newPost)
    }

    fun updatePost(existingPost: Post) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePost(existingPost)
    }

    fun deletePost(existingPost: Post) = viewModelScope.launch(Dispatchers.IO) {
        repository.deletePost(existingPost)
    }

    fun searchPost(query: String): LiveData<List<Post>>{
        return repository.searchPost(query)
    }

    fun getAllPost(): LiveData<List<Post>>{
        return repository.getPost()
    }

}