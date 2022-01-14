package com.amanaggarwal1.pratilipi.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amanaggarwal1.pratilipi.model.Post

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Query("SELECT * FROM Post ORDER BY date DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM post WHERE title LIKE :query OR description LIKE :query OR date LIKE :query ORDER BY date DESC")
    fun searchPost(query: String): LiveData<List<Post>>

    @Delete
    fun deletePost(post: Post)
}