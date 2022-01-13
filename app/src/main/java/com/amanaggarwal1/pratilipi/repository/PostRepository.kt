package com.amanaggarwal1.pratilipi.repository

import com.amanaggarwal1.pratilipi.db.PostDatabase
import com.amanaggarwal1.pratilipi.model.Post

class PostRepository(private val db: PostDatabase) {
    suspend fun addPost(post: Post) = db.getPostDao().insertPost(post)
    suspend fun updatePost(post: Post) = db.getPostDao().updatePost(post)
    suspend fun deletePost(post: Post) = db.getPostDao().deletePost(post)
    fun getPost() = db.getPostDao().getAllPosts()
    fun searchPost(query: String) = db.getPostDao().searchPost(query)

}