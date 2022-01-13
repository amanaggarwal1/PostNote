package com.amanaggarwal1.pratilipi.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amanaggarwal1.pratilipi.repository.PostRepository

class PostActivityViewModelFactory(private val repository: PostRepository):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostActivityViewModel(repository) as T
    }
}